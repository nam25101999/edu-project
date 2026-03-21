package com.edu.university.modules.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    Page<AuditLog> findByStatus(String status, Pageable pageable);

    // ANALYTICS: Thống kê số lượng thao tác theo từng trạng thái (SUCCESS / FAILED)
    @Query("SELECT a.status as status, COUNT(a) as count FROM AuditLog a GROUP BY a.status")
    List<Map<String, Object>> countLogsByStatus();

    // ANALYTICS: Lấy danh sách 10 API chạy chậm nhất (Dùng để tối ưu hệ thống)
    @Query("SELECT a FROM AuditLog a ORDER BY a.executionTimeMs DESC LIMIT 10")
    List<AuditLog> findTop10SlowestOperations();

    // ANALYTICS: Thống kê thao tác theo Entity
    @Query("SELECT a.entityName as entity, COUNT(a) as count FROM AuditLog a GROUP BY a.entityName")
    List<Map<String, Object>> countLogsByEntityName();
}