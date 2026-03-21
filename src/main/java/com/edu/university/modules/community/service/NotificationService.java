package com.edu.university.modules.community.service;

import com.edu.university.modules.community.entity.Notification;
import com.edu.university.modules.community.entity.NotificationType;
import com.edu.university.modules.enrollment.repository.EnrollmentRepository;
import com.edu.university.modules.community.controller.NotificationRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final StudentRepository studentRepo;

    // ======================================
    // GỬI THÔNG BÁO TOÀN TRƯỜNG
    // ======================================
    @Transactional
    public Notification sendToAll(String title, String message) {
        Notification notif = Notification.builder()
                .title(title)
                .message(message)
                .type(NotificationType.TOAN_TRUONG)
                .targetId(null)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepo.save(notif);
    }

    // ======================================
    // GỬI THÔNG BÁO THEO LỚP HỌC PHẦN
    // ======================================
    @Transactional
    public Notification sendToClass(UUID classSectionId, String title, String message) {
        Notification notif = Notification.builder()
                .title(title)
                .message(message)
                .type(NotificationType.THEO_LOP)
                .targetId(classSectionId)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepo.save(notif);
    }

    // ======================================
    // GỬI THÔNG BÁO ĐẾN CÁ NHÂN
    // ======================================
    @Transactional
    public Notification sendToUser(UUID userId, String title, String message) {
        Notification notif = Notification.builder()
                .title(title)
                .message(message)
                .type(NotificationType.CA_NHAN)
                .targetId(userId)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepo.save(notif);
    }

    // ======================================
    // LẤY TẤT CẢ THÔNG BÁO CỦA SINH VIÊN
    // ======================================
    public List<Notification> getMyNotifications(UUID userId) {
        List<Notification> allMyNotifs = new ArrayList<>();

        // 1. Thông báo toàn trường
        allMyNotifs.addAll(notificationRepo.findByTypeOrderByCreatedAtDesc(NotificationType.TOAN_TRUONG));

        // 2. Thông báo cá nhân
        allMyNotifs.addAll(notificationRepo.findByTypeAndTargetIdOrderByCreatedAtDesc(NotificationType.CA_NHAN, userId));

        // 3. Thông báo theo lớp học phần
        studentRepo.findByUserId(userId).ifPresent(student -> {
            List<UUID> enrolledClassIds = enrollmentRepo.findByStudentId(student.getId())
                    .stream()
                    .map(e -> e.getClassSection().getId())
                    .toList();

            if (!enrolledClassIds.isEmpty()) {
                allMyNotifs.addAll(notificationRepo.findByTypeAndTargetIdInOrderByCreatedAtDesc(NotificationType.THEO_LOP, enrolledClassIds));
            }
        });

        // Sắp xếp theo thời gian giảm dần
        allMyNotifs.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        return allMyNotifs;
    }
}