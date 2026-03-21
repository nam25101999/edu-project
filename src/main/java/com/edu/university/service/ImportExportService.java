package com.edu.university.service;

import com.edu.university.entity.*;
import com.edu.university.repository.*;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class ImportExportService {

    private final StudentRepository studentRepo;
    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PasswordEncoder passwordEncoder;

    // ======================================
    // 1. IMPORT SINH VIÊN TỪ EXCEL
    // ======================================
    @Transactional
    public int importStudentsFromExcel(MultipartFile file) throws IOException {
        int count = 0;
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua header

                String username = getCellValueAsString(row.getCell(0));
                String password = getCellValueAsString(row.getCell(1));
                String email = getCellValueAsString(row.getCell(2));
                String studentCode = getCellValueAsString(row.getCell(3));
                String fullName = getCellValueAsString(row.getCell(4));
                String yearStr = getCellValueAsString(row.getCell(6));

                if (username.isBlank() || studentCode.isBlank()) continue;

                if (!userRepo.existsByUsername(username) && !studentRepo.existsByStudentCode(studentCode)) {
                    // Tạo user
                    User user = User.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .email(email)
                            .role(Role.ROLE_STUDENT)
                            .build();
                    user = userRepo.save(user);

                    // Tạo sinh viên
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
        }
        return count;
    }

    // ======================================
    // 2. EXPORT DANH SÁCH SINH VIÊN
    // ======================================
    public byte[] exportStudentList() throws IOException {
        List<Student> students = studentRepo.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Students");

            // Header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("STT");
            header.createCell(1).setCellValue("Mã SV");
            header.createCell(2).setCellValue("Họ Tên");
            header.createCell(3).setCellValue("Email");
            header.createCell(4).setCellValue("Khoa");
            header.createCell(5).setCellValue("Khóa");

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

    // ======================================
    // 3. EXPORT BẢNG ĐIỂM CHI TIẾT
    // ======================================
    public byte[] exportStudentGrades(UUID studentId) throws IOException {
        List<Grade> grades = gradeRepo.findByEnrollmentStudentId(studentId);

        // Nhóm theo học kỳ
        Map<String, List<Grade>> gradesBySemester = grades.stream()
                .collect(Collectors.groupingBy(g ->
                        g.getEnrollment().getClassSection().getSemester() + " - " +
                                g.getEnrollment().getClassSection().getYear()));

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Transcript");
            int rowIdx = 0;

            // Tính GPA tích lũy
            Map<UUID, Double> maxGpaPerCourse = new HashMap<>();
            Map<UUID, Integer> creditsPerCourse = new HashMap<>();
            for (Grade g : grades) {
                if (g.getGpaScore() != null) {
                    Course course = g.getEnrollment().getClassSection().getCourse();
                    UUID courseId = course.getId();
                    double currentGpa = g.getGpaScore();
                    if (!maxGpaPerCourse.containsKey(courseId) || currentGpa > maxGpaPerCourse.get(courseId)) {
                        maxGpaPerCourse.put(courseId, currentGpa);
                        creditsPerCourse.put(courseId, course.getCredits());
                    }
                }
            }

            double totalCumulativePoints = 0;
            int totalCumulativeCredits = 0;
            for (UUID courseId : maxGpaPerCourse.keySet()) {
                totalCumulativePoints += maxGpaPerCourse.get(courseId) * creditsPerCourse.get(courseId);
                totalCumulativeCredits += creditsPerCourse.get(courseId);
            }
            double cumulativeGPA = totalCumulativeCredits == 0 ? 0.0
                    : Math.round((totalCumulativePoints / totalCumulativeCredits) * 100.0) / 100.0;

            // Xuất điểm theo học kỳ
            for (Map.Entry<String, List<Grade>> entry : gradesBySemester.entrySet()) {
                Row semRow = sheet.createRow(rowIdx++);
                semRow.createCell(0).setCellValue("HỌC KỲ: " + entry.getKey());

                Row header = sheet.createRow(rowIdx++);
                header.createCell(0).setCellValue("Mã Môn");
                header.createCell(1).setCellValue("Tên Môn");
                header.createCell(2).setCellValue("Tín Chỉ");
                header.createCell(3).setCellValue("Chuyên Cần (10%)");
                header.createCell(4).setCellValue("Giữa Kỳ (30%)");
                header.createCell(5).setCellValue("Cuối Kỳ (60%)");
                header.createCell(6).setCellValue("Tổng Kết (Hệ 10)");
                header.createCell(7).setCellValue("Điểm Chữ");
                header.createCell(8).setCellValue("Điểm Hệ 4");

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
                Row semSummaryRow = sheet.createRow(rowIdx++);
                semSummaryRow.createCell(0).setCellValue(
                        "=> TỔNG KẾT HỌC KỲ | Số tín chỉ đạt: " + semCredits + " | GPA Học kỳ: " + semGPA
                );
                rowIdx++;
            }

            Row cumRow = sheet.createRow(rowIdx++);
            cumRow.createCell(0).setCellValue("====== TỔNG KẾT TOÀN KHÓA ======");
            Row cumDataRow = sheet.createRow(rowIdx++);
            cumDataRow.createCell(0).setCellValue("Tổng số tín chỉ tích lũy: " + totalCumulativeCredits);
            cumDataRow.createCell(1).setCellValue("GPA Tích Lũy: " + cumulativeGPA);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // ======================================
    // 4. EXPORT THỜI KHÓA BIỂU
    // ======================================
    public byte[] exportStudentSchedule(UUID studentId) throws IOException {
        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(studentId);
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Schedule");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Học Kỳ");
            header.createCell(1).setCellValue("Năm Học");
            header.createCell(2).setCellValue("Mã Môn");
            header.createCell(3).setCellValue("Tên Môn");
            header.createCell(4).setCellValue("Tín Chỉ");
            header.createCell(5).setCellValue("Lịch Học");
            header.createCell(6).setCellValue("Phòng Học");

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

    // ======================================
    // HELPER
    // ======================================
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