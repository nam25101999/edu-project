package com.edu.university.modules.report.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.enrollment.repository.EnrollmentRepository;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import com.edu.university.modules.course.entity.ClassSection;
import com.edu.university.modules.course.entity.Course;
import com.edu.university.modules.enrollment.entity.Enrollment;
import com.edu.university.modules.enrollment.entity.Grade;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service xử lý Import/Export dữ liệu hệ thống (Excel).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportExportService {

    private final StudentRepository studentRepo;
    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PasswordEncoder passwordEncoder;

    @LogAction(action = "IMPORT_STUDENTS", entityName = "STUDENT")
    @Transactional
    public int importStudentsFromExcel(MultipartFile file) {
        int count = 0;
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String username = getCellValueAsString(row.getCell(0));
                String password = getCellValueAsString(row.getCell(1));
                String email = getCellValueAsString(row.getCell(2));
                String studentCode = getCellValueAsString(row.getCell(3));
                String fullName = getCellValueAsString(row.getCell(4));
                String yearStr = getCellValueAsString(row.getCell(6));

                if (username.isBlank() || studentCode.isBlank()) continue;

                if (!userRepo.existsByUsername(username) && !studentRepo.existsByStudentCode(studentCode)) {
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
            log.error("Lỗi đọc file Excel: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Không thể đọc tệp tin Excel.");
        }
        return count;
    }

    @LogAction(action = "EXPORT_STUDENTS", entityName = "STUDENT")
    public byte[] exportStudentList() throws IOException {
        List<Student> students = studentRepo.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Students");

            Row header = sheet.createRow(0);
            String[] columns = {"STT", "Mã SV", "Họ Tên", "Email", "Khoa", "Khóa"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (Student s : students) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(s.getStudentCode());
                row.createCell(2).setCellValue(s.getFullName());
                row.createCell(3).setCellValue(s.getUser() != null ? s.getUser().getEmail() : "");
                row.createCell(4).setCellValue(
                        s.getMajor() != null && s.getMajor().getFaculty() != null
                                ? s.getMajor().getFaculty().getName()
                                : ""
                );
                row.createCell(5).setCellValue(s.getEnrollmentYear() != null ? s.getEnrollmentYear() : 0);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @LogAction(action = "EXPORT_TRANSCRIPT", entityName = "GRADE")
    public byte[] exportStudentGrades(UUID studentId) throws IOException {
        if (!studentRepo.existsById(studentId)) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }

        List<Grade> grades = gradeRepo.findByEnrollmentStudentId(studentId);

        Map<String, List<Grade>> gradesBySemester = grades.stream()
                .collect(Collectors.groupingBy(g ->
                        g.getEnrollment().getClassSection().getSemester() + " - " +
                                g.getEnrollment().getClassSection().getYear()));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Transcript");
            int rowIdx = 0;

            // Logic tính GPA tích lũy tương tự như trong GradeService
            Map<UUID, Double> maxGpaPerCourse = new HashMap<>();
            Map<UUID, Integer> creditsPerCourse = new HashMap<>();
            for (Grade g : grades) {
                if (g.getGpaScore() != null) {
                    Course course = g.getEnrollment().getClassSection().getCourse();
                    if (!maxGpaPerCourse.containsKey(course.getId()) || g.getGpaScore() > maxGpaPerCourse.get(course.getId())) {
                        maxGpaPerCourse.put(course.getId(), g.getGpaScore());
                        creditsPerCourse.put(course.getId(), course.getCredits());
                    }
                }
            }

            double totalPoints = 0;
            int totalCredits = 0;
            for (UUID cid : maxGpaPerCourse.keySet()) {
                totalPoints += maxGpaPerCourse.get(cid) * creditsPerCourse.get(cid);
                totalCredits += creditsPerCourse.get(cid);
            }
            double cumulativeGPA = totalCredits == 0 ? 0.0 : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;

            for (Map.Entry<String, List<Grade>> entry : gradesBySemester.entrySet()) {
                Row semRow = sheet.createRow(rowIdx++);
                semRow.createCell(0).setCellValue("HỌC KỲ: " + entry.getKey());

                Row header = sheet.createRow(rowIdx++);
                String[] cols = {"Mã Môn", "Tên Môn", "Tín Chỉ", "CC(10%)", "GK(30%)", "CK(60%)", "Tổng Kết", "Điểm Chữ", "Hệ 4"};
                for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);

                double semPoints = 0;
                int semCredits = 0;

                for (Grade g : entry.getValue()) {
                    Row row = sheet.createRow(rowIdx++);
                    Course course = g.getEnrollment().getClassSection().getCourse();
                    row.createCell(0).setCellValue(course.getCourseCode());
                    row.createCell(1).setCellValue(course.getName());
                    row.createCell(2).setCellValue(course.getCredits());
                    row.createCell(3).setCellValue(g.getAttendanceScore() != null ? g.getAttendanceScore() : 0);
                    row.createCell(4).setCellValue(g.getMidtermScore() != null ? g.getMidtermScore() : 0);
                    row.createCell(5).setCellValue(g.getFinalScore() != null ? g.getFinalScore() : 0);
                    row.createCell(6).setCellValue(g.getTotalScore() != null ? g.getTotalScore() : 0);
                    row.createCell(7).setCellValue(g.getLetterGrade() != null ? g.getLetterGrade() : "");
                    row.createCell(8).setCellValue(g.getGpaScore() != null ? g.getGpaScore() : 0);

                    if (g.getGpaScore() != null) {
                        semPoints += g.getGpaScore() * course.getCredits();
                        semCredits += course.getCredits();
                    }
                }

                double semGPA = semCredits == 0 ? 0.0 : Math.round((semPoints / semCredits) * 100.0) / 100.0;
                Row summary = sheet.createRow(rowIdx++);
                summary.createCell(0).setCellValue("=> GPA Học kỳ: " + semGPA + " | Tín chỉ đạt: " + semCredits);
                rowIdx++;
            }

            Row totalRow = sheet.createRow(rowIdx++);
            totalRow.createCell(0).setCellValue("GPA TÍCH LŨY TOÀN KHÓA: " + cumulativeGPA + " | Tổng tín chỉ: " + totalCredits);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    @LogAction(action = "EXPORT_SCHEDULE", entityName = "ENROLLMENT")
    public byte[] exportStudentSchedule(UUID studentId) throws IOException {
        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(studentId);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Schedule");
            Row header = sheet.createRow(0);
            String[] cols = {"Học Kỳ", "Năm Học", "Mã Môn", "Tên Môn", "Tín Chỉ", "Lịch Học", "Phòng"};
            for (int i = 0; i < cols.length; i++) header.createCell(i).setCellValue(cols[i]);

            int rowIdx = 1;
            for (Enrollment e : enrollments) {
                Row row = sheet.createRow(rowIdx++);
                ClassSection section = e.getClassSection();
                row.createCell(0).setCellValue(section.getSemester());
                row.createCell(1).setCellValue(section.getYear());
                row.createCell(2).setCellValue(section.getCourse().getCourseCode());
                row.createCell(3).setCellValue(section.getCourse().getName());
                row.createCell(4).setCellValue(section.getCourse().getCredits());
                row.createCell(5).setCellValue(section.getSchedule());
                row.createCell(6).setCellValue(section.getRoom());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}