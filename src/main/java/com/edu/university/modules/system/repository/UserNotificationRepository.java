package com.edu.university.modules.system.repository;

import com.edu.university.modules.system.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, UUID> {
    Page<UserNotification> findByUserId(UUID userId, Pageable pageable);
}
