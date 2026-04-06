package com.edu.university.modules.auth.controller;

import com.edu.university.common.response.BaseResponse;
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
 * Controller quáº£n lÃ½ nháº­t kÃ½ há»‡ thá»‘ng vÃ  phÃ¢n tÃ­ch dá»¯ liá»‡u váº­n hÃ nh.
 * Chá»‰ dÃ nh cho quáº£n trá»‹ viÃªn (Admin).
 */
@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    // ================= LIST LOG =================

    @GetMapping
    public BaseResponse<Page<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String status) {

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending() // Sáº¯p xáº¿p má»›i nháº¥t lÃªn Ä‘áº§u
        );

        Page<AuditLog> result;

        if (status != null && status.equalsIgnoreCase("FAILED")) {
            // Frontend truyá»n status=FAILED -> Láº¥y cÃ¡c log HTTP Error >= 400
            result = auditLogService.getFailedLogs(pageRequest);
        } else if (username != null && !username.isBlank()) {
            // Frontend truyá»n username -> tra cá»©u báº±ng username
            result = auditLogService.searchLogsByUsername(username, pageRequest);
        } else {
            // KhÃ´ng truyá»n gÃ¬ -> Láº¥y táº¥t cáº£
            result = auditLogService.getAllLogs(pageRequest);
        }

        return BaseResponse.ok(result);
    }

    // ================= ANALYTICS =================

    /**
     * Thá»‘ng kÃª sá»‘ lÆ°á»£ng log theo tráº¡ng thÃ¡i (HTTP Status Code: 200, 400, 404, 500...)
     */
    @GetMapping("/analytics/status")
    public BaseResponse<List<AuditLogRepository.StatusCount>> getStatusStats() {
        return BaseResponse.ok(
                "Thá»‘ng kÃª tráº¡ng thÃ¡i há»‡ thá»‘ng",
                auditLogService.getStatusStatistics()
        );
    }

    /**
     * Thá»‘ng kÃª sá»‘ lÆ°á»£ng log theo entity (USER, COURSE, ROLE...)
     */
    @GetMapping("/analytics/entities")
    public BaseResponse<List<AuditLogRepository.EntityCount>> getEntityStats() {
        return BaseResponse.ok(
                "Thá»‘ng kÃª tÃ i nguyÃªn há»‡ thá»‘ng",
                auditLogService.getEntityStatistics()
        );
    }

    /**
     * Top 10 API cháº¡y cháº­m nháº¥t Ä‘á»ƒ tá»‘i Æ°u hiá»‡u nÄƒng
     */
    @GetMapping("/analytics/slow-apis")
    public BaseResponse<List<AuditLog>> getSlowestApis() {
        return BaseResponse.ok(
                "Danh sÃ¡ch cÃ¡c thao tÃ¡c pháº£n há»“i cháº­m",
                auditLogService.getSlowestOperations()
        );
    }
}
