package com.edu.university.modules.report.controller;

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

@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class ImportExportController {

    private final ImportExportService importExportService;

    // 1. Import danh sách sinh viên từ file Excel
    @PostMapping(value = "/import/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            int count = importExportService.importStudentsFromExcel(file);
            return ResponseEntity.ok("Đã import thành công " + count + " sinh viên vào hệ thống.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi import file: " + e.getMessage());
        }
    }

    // 2. Export toàn bộ danh sách sinh viên
    @GetMapping("/export/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportStudents() throws IOException {
        byte[] excelContent = importExportService.exportStudentList();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "danh_sach_sinh_vien.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelContent);
    }

    // 3. Export bảng điểm của một sinh viên cụ thể
    @GetMapping("/export/grades/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<byte[]> exportGrades(@PathVariable UUID studentId) throws IOException {
        byte[] excelContent = importExportService.exportStudentGrades(studentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "bang_diem_sinh_vien.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelContent);
    }

    // 4. Export thời khóa biểu của một sinh viên cụ thể
    @GetMapping("/export/schedule/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<byte[]> exportSchedule(@PathVariable UUID studentId) throws IOException {
        byte[] excelContent = importExportService.exportStudentSchedule(studentId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "thoi_khoa_bieu.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelContent);
    }
}