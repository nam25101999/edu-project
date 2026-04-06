package com.edu.university.modules.auth.repository;

import com.edu.university.modules.auth.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    // HÃ m tÃ¬m kiáº¿m theo UUID cá»§a User
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // HÃ m tÃ¬m kiáº¿m theo TÃªn vÃ  ID cá»§a Entity
    Page<AuditLog> findByEntityNameAndEntityIdOrderByCreatedAtDesc(String entityName, String entityId, Pageable pageable);

    // HÃ m tÃ¬m kiáº¿m theo Action vÃ  khoáº£ng thá»i gian
    Page<AuditLog> findByActionAndCreatedAtBetweenOrderByCreatedAtDesc(
            AuditLog.AuditAction action,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    // HÃ m láº¥y táº¥t cáº£ log trong má»™t khoáº£ng thá»i gian
    Page<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    // HÃ m Ä‘áº¿m sá»‘ láº§n thao tÃ¡c cá»§a 1 IP (dÃ¹ng cho Rate Limiting/Block IP)
    long countByIpAddressAndActionAndCreatedAtAfter(
            String ipAddress,
            AuditLog.AuditAction action,
            LocalDateTime since
    );

    // ================== CÃC HÃ€M DÃ™NG CHO PHÃ‚N TÃCH & BÃO CÃO ==================

    // Láº¥y log bá»‹ lá»—i (vÃ­ dá»¥ status >= 400 lÃ  cÃ³ lá»—i Http Bad Request, Server Error, v.v)
    Page<AuditLog> findByStatusGreaterThanEqualOrderByCreatedAtDesc(Integer status, Pageable pageable);

    // Láº¥y top 10 API thá»±c thi cháº­m nháº¥t
    List<AuditLog> findTop10ByOrderByExecutionTimeMsDesc();

    // Interface Ä‘á»ƒ Map káº¿t quáº£ Ä‘áº¿m tráº¡ng thÃ¡i (Analytics)
    interface StatusCount {
        Integer getStatus();
        Long getCount();
    }

    @Query("SELECT a.status as status, COUNT(a) as count FROM AuditLog a GROUP BY a.status")
    List<StatusCount> countLogsByStatus();

    // Interface Ä‘á»ƒ Map káº¿t quáº£ Ä‘áº¿m tÃ i nguyÃªn tÃ¡c Ä‘á»™ng (Analytics)
    interface EntityCount {
        String getEntityName();
        Long getCount();
    }

    @Query("SELECT a.entityName as entityName, COUNT(a) as count FROM AuditLog a WHERE a.entityName IS NOT NULL GROUP BY a.entityName")
    List<EntityCount> countLogsByEntityName();
}
