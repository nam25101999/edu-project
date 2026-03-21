package com.edu.university.modules.community.controller;

import com.edu.university.modules.community.entity.Notification;
import com.edu.university.modules.community.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    // Lấy thông báo theo loại (Dùng cho thông báo toàn trường)
    List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);

    // Lấy thông báo theo loại và mục tiêu (Dùng cho cá nhân)
    List<Notification> findByTypeAndTargetIdOrderByCreatedAtDesc(NotificationType type, UUID targetId);

    // Lấy thông báo theo loại và danh sách mục tiêu (Dùng cho lớp học phần)
    List<Notification> findByTypeAndTargetIdInOrderByCreatedAtDesc(NotificationType type, List<UUID> targetIds);
}