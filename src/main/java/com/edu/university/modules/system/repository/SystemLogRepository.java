package com.edu.university.modules.system.repository;

import com.edu.university.modules.system.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, UUID> {
    Page<SystemLog> findByUserId(UUID userId, Pageable pageable);
    Page<SystemLog> findByTableName(String tableName, Pageable pageable);
}
