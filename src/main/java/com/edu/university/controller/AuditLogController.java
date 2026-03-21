package com.edu.university.controller;

import com.edu.university.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được xem lịch sử hệ thống
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username) {

        // Luôn sắp xếp log mới nhất lên đầu
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (username != null && !username.isBlank()) {
            return ResponseEntity.ok(auditLogService.searchLogsByUsername(username, pageRequest));
        }
        return ResponseEntity.ok(auditLogService.getAllLogs(pageRequest));
    }
}