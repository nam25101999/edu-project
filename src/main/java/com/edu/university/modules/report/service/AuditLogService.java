package com.edu.university.modules.report.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.AuditLog;
import com.edu.university.modules.report.reponsitory.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service xử lý Audit Log + Analytics
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepo;

    // ================= CORE LOG METHOD (QUAN TRỌNG NHẤT) =================
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            String action,
            String entity,
            String status,
            String endpoint,
            String method,
            String username,
            String errorMessage,
            HttpServletRequest request
    ) {
        try {
            AuditLog log = AuditLog.builder()
                    .action(action)
                    .entityName(entity)
                    .status(status)
                    .endpoint(endpoint)
                    .httpMethod(method)
                    .username(username)
                    .errorMessage(errorMessage)
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepo.save(log);

        } catch (Exception e) {
            // ❗ KHÔNG được throw → tránh crash hệ thống chính
            System.err.println("❌ Lỗi khi ghi AuditLog: " + e.getMessage());
        }
    }

    // ================= SAVE LOG (OPTIONAL - giữ lại nếu cần) =================
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(AuditLog auditLog) {
        try {
            if (auditLog == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
            auditLogRepo.save(auditLog);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu AuditLog: " + e.getMessage());
        }
    }

    // ================= QUERY LOG =================

    @Transactional(readOnly = true)
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> searchLogsByUsername(String username, Pageable pageable) {
        return auditLogRepo.findByUsernameContainingIgnoreCase(username, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getFailedLogs(Pageable pageable) {
        return auditLogRepo.findByStatus("FAILED", pageable);
    }

    // ================= ANALYTICS =================

    @Transactional(readOnly = true)
    public List<AuditLogRepository.StatusCount> getStatusStatistics() {
        return auditLogRepo.countLogsByStatus();
    }

    @Transactional(readOnly = true)
    public List<AuditLogRepository.EntityCount> getEntityStatistics() {
        return auditLogRepo.countLogsByEntityName();
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getSlowestOperations() {
        return auditLogRepo.findTop10ByOrderByExecutionTimeMsDesc();
    }
}