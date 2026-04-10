package com.edu.university.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseRepairService {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void repairDatabase() {
        log.info("========== STARTING DATABASE REPAIR ==========");
        try {
            // Drop the check constraint that limits the allowed values for audit 'action'
            // The constraint name was found in the error logs: CK__audit_log__actio__3C75B3E8
            jdbcTemplate.execute("ALTER TABLE audit_logs DROP CONSTRAINT IF EXISTS CK__audit_log__actio__3C75B3E8");
            log.info("✅ Successfully dropped constraint CK__audit_log__actio__3C75B3E8 if it existed.");
        } catch (Exception e) {
            log.warn("⚠️ Failed to drop constraint CK__audit_log__actio__3C75B3E8. It might already be gone. Error: {}", e.getMessage());
        }
        log.info("========== DATABASE REPAIR COMPLETED ==========");
    }
}
