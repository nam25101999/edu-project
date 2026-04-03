package com.edu.university.modules.auth.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.auth.entity.AuditLog;
import com.edu.university.modules.auth.repository.AuditLogRepository;
import com.edu.university.modules.auth.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // ================= LIST LOG =================

    @GetMapping
    public ApiResponse<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending() // Sắp xếp mới nhất lên đầu
        );

        Page<AuditLog> result;

        if (status != null && status.equalsIgnoreCase("FAILED")) {
            // Frontend truyền status=FAILED -> Lấy các log HTTP Error >= 400
            result = auditLogService.getFailedLogs(pageRequest);
        } else if (username != null && !username.isBlank()) {
            // Frontend truyền username -> tra cứu bằng username
            result = auditLogService.searchLogsByUsername(username, pageRequest);
        } else {
            // Không truyền gì -> Lấy tất cả
            result = auditLogService.getAllLogs(pageRequest);
        }

        return ApiResponse.success(result);
    }

    // ================= ANALYTICS =================

    /**
     * Thống kê số lượng log theo trạng thái (HTTP Status Code: 200, 400, 404, 500...)
     */
    @GetMapping("/analytics/status")
    public ApiResponse<List<AuditLogRepository.StatusCount>> getStatusStats() {
        return ApiResponse.success(
                "Thống kê trạng thái hệ thống",
                auditLogService.getStatusStatistics()
        );
    }

    /**
     * Thống kê số lượng log theo entity (USER, COURSE, ROLE...)
     */
    @GetMapping("/analytics/entities")
    public ApiResponse<List<AuditLogRepository.EntityCount>> getEntityStats() {
        return ApiResponse.success(
                "Thống kê tài nguyên hệ thống",
                auditLogService.getEntityStatistics()
        );
    }

    /**
     * Top 10 API chạy chậm nhất để tối ưu hiệu năng
     */
    @GetMapping("/analytics/slow-apis")
    public ApiResponse<List<AuditLog>> getSlowestApis() {
        return ApiResponse.success(
                "Danh sách các thao tác phản hồi chậm",
                auditLogService.getSlowestOperations()
        );
    }
}