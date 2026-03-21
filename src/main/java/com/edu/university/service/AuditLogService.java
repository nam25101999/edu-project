package com.edu.university.service;

import com.edu.university.entity.AuditLog;
import com.edu.university.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}