package com.edu.university.modules.report.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.report.AuditLog;
import com.edu.university.modules.report.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller quản lý nhật ký hệ thống và phân tích dữ liệu vận hành.
 * Chỉ dành cho quản trị viên (Admin).
 */
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ApiResponse<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AuditLog> result;
        if (status != null && !status.isBlank()) {
            result = auditLogService.getFailedLogs(pageRequest);
        } else if (username != null && !username.isBlank()) {
            result = auditLogService.searchLogsByUsername(username, pageRequest);
        } else {
            result = auditLogService.getAllLogs(pageRequest);
        }

        return ApiResponse.success(result);
    }

    // === THỐNG KÊ ANALYTICS ===

    @GetMapping("/analytics/status")
    public ApiResponse<List<Map<String, Object>>> getStatusStats() {
        return ApiResponse.success("Thống kê trạng thái hệ thống", auditLogService.getStatusStatistics());
    }

    @GetMapping("/analytics/entities")
    public ApiResponse<List<Map<String, Object>>> getEntityStats() {
        return ApiResponse.success("Thống kê tài nguyên hệ thống", auditLogService.getEntityStatistics());
    }

    @GetMapping("/analytics/slow-apis")
    public ApiResponse<List<AuditLog>> getSlowestApis() {
        return ApiResponse.success("Danh sách các thao tác phản hồi chậm", auditLogService.getSlowestOperations());
    }
}