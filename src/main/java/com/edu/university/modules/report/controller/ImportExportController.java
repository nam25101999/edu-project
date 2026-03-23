package com.edu.university.modules.report.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.report.service.ImportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Controller xử lý xuất nhập dữ liệu Excel.
 * Các API tải file trả về ResponseEntity<byte[]>, các API xử lý dữ liệu trả về ApiResponse.
 */
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class ImportExportController {

    private final ImportExportService importExportService;

    @PostMapping(value = "/import/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Integer> importStudents(@RequestParam("file") MultipartFile file) {
        int count = importExportService.importStudentsFromExcel(file);
        return ApiResponse.success("Đã import thành công " + count + " sinh viên vào hệ thống.", count);
    }

    @GetMapping("/export/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportStudents() throws IOException {
        byte[] excelContent = importExportService.exportStudentList();
        return createExcelResponse(excelContent, "danh_sach_sinh_vien.xlsx");
    }

    @GetMapping("/export/grades/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<byte[]> exportGrades(@PathVariable UUID studentId) throws IOException {
        byte[] excelContent = importExportService.exportStudentGrades(studentId);
        return createExcelResponse(excelContent, "bang_diem_sinh_vien.xlsx");
    }

    @GetMapping("/export/schedule/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<byte[]> exportSchedule(@PathVariable UUID studentId) throws IOException {
        byte[] excelContent = importExportService.exportStudentSchedule(studentId);
        return createExcelResponse(excelContent, "thoi_khoa_bieu.xlsx");
    }

    /**
     * Helper tạo ResponseEntity cho file Excel.
     */
    private ResponseEntity<byte[]> createExcelResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return ResponseEntity.ok().headers(headers).body(content);
    }
}