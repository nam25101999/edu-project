package com.edu.university.modules.enrollment.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.enrollment.dto.GradeResponse;
import com.edu.university.modules.enrollment.entity.Grade;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import com.edu.university.modules.enrollment.service.GradeService;
import com.edu.university.modules.enrollment.dto.GradeRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller quản lý điểm số sinh viên.
 */
@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final GradeRepository gradeRepository;

    @PostMapping("/enrollment/{enrollmentId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public ApiResponse<GradeResponse> enterGrade(@PathVariable UUID enrollmentId, @RequestBody GradeRequest request) {
        // Thay đổi kiểu trả về từ Grade sang GradeResponse
        return ApiResponse.success("Cập nhật điểm thành công", gradeService.enterGrade(enrollmentId, request));
    }

    @GetMapping("/student/{studentId}/gpa")
    public ApiResponse<Map<String, Object>> getStudentGPA(@PathVariable UUID studentId) {
        double gpa = gradeService.calculateCumulativeGPA(studentId);
        return ApiResponse.success(Map.of(
                "studentId", studentId,
                "cumulativeGPA", gpa
        ));
    }

    @GetMapping("/export/class/{classSectionId}")
    @PreAuthorize("hasRole('LECTURER') or hasRole('ADMIN')")
    public void exportGradesToExcel(@PathVariable UUID classSectionId, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=grades_class_" + classSectionId + ".xlsx");

        // Lưu ý: Thực tế nên viết query repo lấy theo classSectionId
        List<Grade> grades = gradeRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Grades");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Mã SV");
            headerRow.createCell(1).setCellValue("Họ Tên");
            headerRow.createCell(2).setCellValue("Điểm Tổng (10)");
            headerRow.createCell(3).setCellValue("Điểm Chữ");

            int rowNum = 1;
            for (Grade grade : grades) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(grade.getEnrollment().getStudent().getStudentCode());
                row.createCell(1).setCellValue(grade.getEnrollment().getStudent().getFullName());
                row.createCell(2).setCellValue(grade.getTotalScore() != null ? grade.getTotalScore() : 0);
                row.createCell(3).setCellValue(grade.getLetterGrade() != null ? grade.getLetterGrade() : "N/A");
            }
            workbook.write(response.getOutputStream());
        }
    }
}