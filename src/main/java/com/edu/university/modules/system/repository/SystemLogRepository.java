package com.edu.university.modules.system.repository;

import com.edu.university.modules.system.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, UUID> {
    List<SystemLog> findByUserId(UUID userId);
    List<SystemLog> findByTableName(String tableName);
}
