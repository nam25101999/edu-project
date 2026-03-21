package com.edu.university.modules.report.controller;

import com.edu.university.modules.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Toàn bộ API báo cáo chỉ dành cho ADMIN
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardOverview() {
        return ResponseEntity.ok(reportService.getDashboardOverview());
    }

    @GetMapping("/faculty-stats")
    public ResponseEntity<?> getStudentsByFaculty() {
        return ResponseEntity.ok(reportService.getStudentsByFaculty());
    }

    @GetMapping("/pass-fail-ratio")
    public ResponseEntity<?> getPassFailRatio() {
        return ResponseEntity.ok(reportService.getPassFailRatio());
    }

    @GetMapping("/top-students")
    public ResponseEntity<?> getTopStudents(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reportService.getTopStudents(limit));
    }
}