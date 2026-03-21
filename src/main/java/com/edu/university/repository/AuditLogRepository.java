package com.edu.university.repository;

import com.edu.university.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    Page<AuditLog> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<AuditLog> findByEntityNameIgnoreCase(String entityName, Pageable pageable);
}