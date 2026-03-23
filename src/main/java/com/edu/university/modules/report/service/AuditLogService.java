package com.edu.university.modules.report.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.AuditLog;
import com.edu.university.modules.report.reponsitory.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service xử lý dữ liệu nhật ký hệ thống (Audit Logs) và thống kê.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepo;

    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepo.findAll(pageable);
    }

    public Page<AuditLog> searchLogsByUsername(String username, Pageable pageable) {
        return auditLogRepo.findByUsernameContainingIgnoreCase(username, pageable);
    }

    public Page<AuditLog> getFailedLogs(Pageable pageable) {
        return auditLogRepo.findByStatus("FAILED", pageable);
    }

    // --- Enterprise Analytics Methods ---

    public List<Map<String, Object>> getStatusStatistics() {
        return auditLogRepo.countLogsByStatus();
    }

    public List<Map<String, Object>> getEntityStatistics() {
        return auditLogRepo.countLogsByEntityName();
    }

    public List<AuditLog> getSlowestOperations() {
        return auditLogRepo.findTop10SlowestOperations();
    }
}