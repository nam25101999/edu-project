package com.edu.university.modules.report.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.enrollment.dto.GradeFullDto;
import com.edu.university.modules.enrollment.repository.EnrollmentRepository;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import com.edu.university.modules.course.entity.ClassSection;
import com.edu.university.modules.enrollment.entity.Enrollment;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportService {

    private final StudentRepository studentRepo;
    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PasswordEncoder passwordEncoder;

    // ==========================================
    // 1. IMPORT STUDENTS
    // ==========================================
    @LogAction(action = "IMPORT_STUDENTS", entityName = "STUDENT")
    @Transactional
    public int importStudentsFromExcel(MultipartFile file) {
        int count = 0;
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String username = getCellValueAsString(row.getCell(1));
                String password = getCellValueAsString(row.getCell(2));
                String studentCode = getCellValueAsString(row.getCell(3));
                String fullName = getCellValueAsString(row.getCell(4));
                String email = getCellValueAsString(row.getCell(5));
                String yearStr = getCellValueAsString(row.getCell(7));

                if (username.isBlank() || studentCode.isBlank()) continue;

                if (!email.isBlank() && !email.contains("@")) {
                    throw new BusinessException(ErrorCode.INVALID_INPUT,
                            "Lỗi tại dòng " + (row.getRowNum() + 1));
                }

                if (!userRepo.existsByUsername(username)
                        && !studentRepo.existsByStudentCode(studentCode)) {

                    if (password.isBlank()) password = studentCode;

                    User user = User.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .email(email)
                            .role(Role.ROLE_STUDENT)
                            .build();
                    user = userRepo.save(user);

                    Student student = Student.builder()
                            .user(user)
                            .studentCode(studentCode)
                            .fullName(fullName)
                            .enrollmentYear(yearStr.isBlank() ? null : Integer.parseInt(yearStr))
                            .build();

                    studentRepo.save(student);
                    count++;
                }
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return count;
    }

    // ==========================================
    // 2. EXPORT STUDENTS
    // ==========================================
    @LogAction(action = "EXPORT_STUDENTS", entityName = "STUDENT")
    public byte[] exportStudentList() throws IOException {

        List<Student> students = studentRepo.findAllWithDetails();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Students");

            Row header = sheet.createRow(0);
            String[] cols = {"STT","Username","Password","Mã SV","Họ Tên","Email","Khoa","Khóa"};

            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int rowIdx = 1;

            for (Student s : students) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(rowIdx - 1);

                // ✅ Sửa lỗi NullPointerException tại đây: Kiểm tra s.getUser()
                String username = (s.getUser() != null) ? s.getUser().getUsername() : "N/A";
                row.createCell(1).setCellValue(username);

                row.createCell(2).setCellValue("***");
                row.createCell(3).setCellValue(s.getStudentCode());
                row.createCell(4).setCellValue(s.getFullName());

                // ✅ Sửa lỗi NullPointerException khi lấy email
                String email = (s.getUser() != null && s.getUser().getEmail() != null)
                        ? s.getUser().getEmail() : "";
                row.createCell(5).setCellValue(email);

                row.createCell(6).setCellValue(
                        s.getMajor() != null && s.getMajor().getFaculty() != null
                                ? s.getMajor().getFaculty().getName() : ""
                );
                row.createCell(7).setCellValue(
                        s.getEnrollmentYear() != null ? s.getEnrollmentYear() : 0
                );
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ==========================================
    // 3. EXPORT TRANSCRIPT
    // ==========================================
    @LogAction(action = "EXPORT_TRANSCRIPT", entityName = "GRADE")
    public byte[] exportStudentGrades(UUID studentId) throws IOException {

        if (!studentRepo.existsById(studentId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        List<GradeFullDto> grades = gradeRepo.findFullGradeData(studentId);

        Map<String, List<GradeFullDto>> gradesBySemester = grades.stream()
                .collect(Collectors.groupingBy(g -> g.semester() + " - " + g.year()));

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Transcript");
            int rowIdx = 0;

            // ===== GPA tích lũy =====
            Map<String, Double> maxGpaPerCourse = new HashMap<>();
            Map<String, Integer> creditsPerCourse = new HashMap<>();

            for (GradeFullDto g : grades) {
                if (g.gpaScore() != null) {
                    String key = g.courseCode();

                    if (!maxGpaPerCourse.containsKey(key)
                            || g.gpaScore() > maxGpaPerCourse.get(key)) {

                        maxGpaPerCourse.put(key, g.gpaScore());
                        creditsPerCourse.put(key, g.credits());
                    }
                }
            }

            double totalPoints = 0;
            int totalCredits = 0;

            for (String key : maxGpaPerCourse.keySet()) {
                totalPoints += maxGpaPerCourse.get(key) * creditsPerCourse.get(key);
                totalCredits += creditsPerCourse.get(key);
            }

            double cumulativeGPA = totalCredits == 0 ? 0.0
                    : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;

            // ===== từng học kỳ =====
            for (Map.Entry<String, List<GradeFullDto>> entry : gradesBySemester.entrySet()) {

                Row semRow = sheet.createRow(rowIdx++);
                semRow.createCell(0).setCellValue("HỌC KỲ: " + entry.getKey());

                Row header = sheet.createRow(rowIdx++);
                String[] cols = {"Mã","Tên","TC","CC","GK","CK","Tổng","Chữ","Hệ 4"};

                for (int i = 0; i < cols.length; i++) {
                    header.createCell(i).setCellValue(cols[i]);
                }

                double semPoints = 0;
                int semCredits = 0;

                for (GradeFullDto g : entry.getValue()) {
                    Row row = sheet.createRow(rowIdx++);

                    row.createCell(0).setCellValue(g.courseCode());
                    row.createCell(1).setCellValue(g.courseName());
                    row.createCell(2).setCellValue(g.credits());
                    row.createCell(3).setCellValue(g.attendance() != null ? g.attendance() : 0);
                    row.createCell(4).setCellValue(g.midterm() != null ? g.midterm() : 0);
                    row.createCell(5).setCellValue(g.finalScore() != null ? g.finalScore() : 0);
                    row.createCell(6).setCellValue(g.totalScore() != null ? g.totalScore() : 0);
                    row.createCell(7).setCellValue(g.letterGrade() != null ? g.letterGrade() : "");
                    row.createCell(8).setCellValue(g.gpaScore() != null ? g.gpaScore() : 0);

                    if (g.gpaScore() != null) {
                        semPoints += g.gpaScore() * g.credits();
                        semCredits += g.credits();
                    }
                }

                double semGPA = semCredits == 0 ? 0.0
                        : Math.round((semPoints / semCredits) * 100.0) / 100.0;

                Row sum = sheet.createRow(rowIdx++);
                sum.createCell(0).setCellValue("GPA HK: " + semGPA + " | TC: " + semCredits);

                rowIdx++;
            }

            Row total = sheet.createRow(rowIdx++);
            total.createCell(0).setCellValue("GPA TÍCH LŨY: " + cumulativeGPA);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ==========================================
    // 4. EXPORT SCHEDULE
    // ==========================================
    @LogAction(action = "EXPORT_SCHEDULE", entityName = "ENROLLMENT")
    public byte[] exportStudentSchedule(UUID studentId) throws IOException {

        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(studentId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Schedule");

            Row header = sheet.createRow(0);
            String[] cols = {"HK","Năm","Mã","Tên","TC","Lịch","Phòng"};

            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            int rowIdx = 1;

            for (Enrollment e : enrollments) {
                Row row = sheet.createRow(rowIdx++);

                ClassSection s = e.getClassSection();

                row.createCell(0).setCellValue(s.getSemester());
                row.createCell(1).setCellValue(s.getYear());
                row.createCell(2).setCellValue(s.getCourse().getCourseCode());
                row.createCell(3).setCellValue(s.getCourse().getName());
                row.createCell(4).setCellValue(s.getCourse().getCredits());
                row.createCell(5).setCellValue(s.getSchedule());
                row.createCell(6).setCellValue(s.getRoom());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ==========================================
    // UTIL
    // ==========================================
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                yield (v == Math.floor(v)) ? String.valueOf((long) v) : String.valueOf(v);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}