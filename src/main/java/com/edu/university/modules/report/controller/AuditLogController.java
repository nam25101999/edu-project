package com.edu.university.modules.report.controller;

import com.edu.university.modules.report.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Phân quyền cực kỳ bảo mật
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(auditLogService.getFailedLogs(pageRequest));
        }
        if (username != null && !username.isBlank()) {
            return ResponseEntity.ok(auditLogService.searchLogsByUsername(username, pageRequest));
        }
        return ResponseEntity.ok(auditLogService.getAllLogs(pageRequest));
    }

    // === THỐNG KÊ ANALYTICS ===

    @GetMapping("/analytics/status")
    public ResponseEntity<?> getStatusStats() {
        return ResponseEntity.ok(auditLogService.getStatusStatistics());
    }

    @GetMapping("/analytics/entities")
    public ResponseEntity<?> getEntityStats() {
        return ResponseEntity.ok(auditLogService.getEntityStatistics());
    }

    @GetMapping("/analytics/slow-apis")
    public ResponseEntity<?> getSlowestApis() {
        // Hỗ trợ Dev/Admin phát hiện các hàm bị "nghẽn cổ chai" (Bottleneck)
        return ResponseEntity.ok(auditLogService.getSlowestOperations());
    }
}