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

    // Hàm tìm kiếm theo UUID của User
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // Hàm tìm kiếm theo Tên và ID của Entity
    Page<AuditLog> findByEntityNameAndEntityIdOrderByCreatedAtDesc(String entityName, String entityId, Pageable pageable);

    // Hàm tìm kiếm theo Action và khoảng thời gian
    Page<AuditLog> findByActionAndCreatedAtBetweenOrderByCreatedAtDesc(
            AuditLog.AuditAction action,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    // Hàm lấy tất cả log trong một khoảng thời gian
    Page<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    // Hàm đếm số lần thao tác của 1 IP (dùng cho Rate Limiting/Block IP)
    long countByIpAddressAndActionAndCreatedAtAfter(
            String ipAddress,
            AuditLog.AuditAction action,
            LocalDateTime since
    );

    // ================== CÁC HÀM DÙNG CHO PHÂN TÍCH & BÁO CÁO ==================

    // Lấy log bị lỗi (ví dụ status >= 400 là có lỗi Http Bad Request, Server Error, v.v)
    Page<AuditLog> findByStatusGreaterThanEqualOrderByCreatedAtDesc(Integer status, Pageable pageable);

    // Lấy top 10 API thực thi chậm nhất
    List<AuditLog> findTop10ByOrderByExecutionTimeMsDesc();

    // Interface để Map kết quả đếm trạng thái (Analytics)
    interface StatusCount {
        Integer getStatus();
        Long getCount();
    }

    @Query("SELECT a.status as status, COUNT(a) as count FROM AuditLog a GROUP BY a.status")
    List<StatusCount> countLogsByStatus();

    // Interface để Map kết quả đếm tài nguyên tác động (Analytics)
    interface EntityCount {
        String getEntityName();
        Long getCount();
    }

    @Query("SELECT a.entityName as entityName, COUNT(a) as count FROM AuditLog a WHERE a.entityName IS NOT NULL GROUP BY a.entityName")
    List<EntityCount> countLogsByEntityName();
}