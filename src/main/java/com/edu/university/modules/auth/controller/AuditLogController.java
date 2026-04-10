package com.edu.university.modules.auth.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.auth.dto.AuditLogResponseDTO;
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
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    // ================= LIST LOG =================

    @GetMapping
    public BaseResponse<PageResponse<AuditLogResponseDTO>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<AuditLogResponseDTO> result;

        if (status != null && status.equalsIgnoreCase("FAILED")) {
            result = auditLogService.getFailedLogs(pageRequest);
        } else if (username != null && !username.isBlank()) {
            result = auditLogService.searchLogsByUsername(username, pageRequest);
        } else {
            result = auditLogService.getAllLogs(pageRequest);
        }

        return BaseResponse.okPage(result);
    }

    // ================= ANALYTICS =================

    /**
     * Thống kê số lượng log theo trạng thái (HTTP Status Code: 200, 400, 404, 500...)
     */
    @GetMapping("/analytics/status")
    public BaseResponse<List<AuditLogRepository.StatusCount>> getStatusStats() {
        return BaseResponse.ok(auditLogService.getStatusStatistics());
    }

    @GetMapping("/analytics/entities")
    public BaseResponse<List<AuditLogRepository.EntityCount>> getEntityStats() {
        return BaseResponse.ok(auditLogService.getEntityStatistics());
    }

    @GetMapping("/analytics/slow-apis")
    public BaseResponse<List<AuditLogResponseDTO>> getSlowestApis() {
        return BaseResponse.ok(auditLogService.getSlowestOperations());
    }
}
