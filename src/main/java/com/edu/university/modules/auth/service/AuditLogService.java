package com.edu.university.modules.auth.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.AuditLog;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.AuditLogRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service xá»­ lÃ½ Audit Log + Analytics
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepo;
    private final UserRepository userRepository; // DÃ¹ng Ä‘á»ƒ tÃ¬m userId tá»« username

    // ================= CORE LOG METHOD (QUAN TRá»ŒNG NHáº¤T) =================
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            AuditLog.AuditAction action,
            String entity,
            String entityId,
            Integer status,
            String endpoint,
            String method,
            UUID userId,
            String errorMessage,
            HttpServletRequest request
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .entityName(entity)
                    .entityId(entityId)
                    .status(status)
                    .endpoint(endpoint)
                    .httpMethod(method)
                    .userId(userId)
                    .errorMessage(errorMessage)
                    .ipAddress(request != null ? request.getRemoteAddr() : "Unknown")
                    .userAgent(request != null ? request.getHeader("User-Agent") : "Unknown")
                    .build();

            auditLogRepo.save(auditLog);

        } catch (Exception e) {
            log.error("âŒ Lá»—i khi ghi AuditLog: {}", e.getMessage());
        }
    }

    // ================= SAVE LOG =================
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(AuditLog auditLog) {
        try {
            if (auditLog == null) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
            auditLogRepo.save(auditLog);
        } catch (Exception e) {
            log.error("âŒ Lá»—i khi lÆ°u AuditLog: {}", e.getMessage());
        }
    }

    // ================= QUERY LOG =================

    @Transactional(readOnly = true)
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getLogsByUserId(UUID userId, Pageable pageable) {
        return auditLogRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getLogsByEntity(String entityName, String entityId, Pageable pageable) {
        return auditLogRepo.findByEntityNameAndEntityIdOrderByCreatedAtDesc(entityName, entityId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> searchLogsByUsername(String username, Pageable pageable) {
        // Chuyá»ƒn tá»« username -> userId Ä‘á»ƒ truy váº¥n
        Optional<Users> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return auditLogRepo.findByUserIdOrderByCreatedAtDesc(userOpt.get().getId(), pageable);
        }
        return Page.empty(pageable); // Tráº£ vá» list rá»—ng náº¿u khÃ´ng tÃ¬m tháº¥y user
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getFailedLogs(Pageable pageable) {
        // Lá»c cÃ¡c log cÃ³ mÃ£ HTTP Status >= 400 (Client Error hoáº·c Server Error)
        return auditLogRepo.findByStatusGreaterThanEqualOrderByCreatedAtDesc(400, pageable);
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
