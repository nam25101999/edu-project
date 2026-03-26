package com.edu.university.modules.report.reponsitory;

import com.edu.university.modules.report.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<AuditLog> findByStatus(String status, Pageable pageable);

    // ================= PROJECTION =================

    interface StatusCount {
        String getStatus();
        Long getCount();
    }

    interface EntityCount {
        String getEntity();
        Long getCount();
    }

    // ================= ANALYTICS =================

    @Query("""
        SELECT a.status as status, COUNT(a) as count
        FROM AuditLog a
        GROUP BY a.status
    """)
    List<StatusCount> countLogsByStatus();

    @Query("""
        SELECT a.entityName as entity, COUNT(a) as count
        FROM AuditLog a
        GROUP BY a.entityName
    """)
    List<EntityCount> countLogsByEntityName();

    // ❌ KHÔNG dùng LIMIT trong JPQL
    List<AuditLog> findTop10ByOrderByExecutionTimeMsDesc();
}