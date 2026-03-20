package com.edu.university.service;

import com.edu.university.entity.Enrollment;
import com.edu.university.entity.Notification;
import com.edu.university.entity.NotificationType;
import com.edu.university.entity.Student;
import com.edu.university.repository.EnrollmentRepository;
import com.edu.university.repository.NotificationRepository;
import com.edu.university.repository.StudentRepository;
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

    // Lấy toàn bộ thông báo hiển thị cho 1 user đăng nhập
    public List<Notification> getMyNotifications(UUID userId) {
        List<Notification> allMyNotifs = new ArrayList<>();

        // 1. Lấy thông báo toàn trường
        allMyNotifs.addAll(notificationRepo.findByTypeOrderByCreatedAtDesc(NotificationType.TOAN_TRUONG));

        // 2. Lấy thông báo cá nhân
        allMyNotifs.addAll(notificationRepo.findByTypeAndTargetIdOrderByCreatedAtDesc(NotificationType.CA_NHAN, userId));

        // 3. Lấy thông báo của các lớp học phần mà sinh viên này đang học
        studentRepo.findByUserId(userId).ifPresent(student -> {
            List<UUID> enrolledClassIds = enrollmentRepo.findByStudentId(student.getId())
                    .stream()
                    .map(e -> e.getClassSection().getId())
                    .toList();

            if (!enrolledClassIds.isEmpty()) {
                allMyNotifs.addAll(notificationRepo.findByTypeAndTargetIdInOrderByCreatedAtDesc(NotificationType.THEO_LOP, enrolledClassIds));
            }
        });

        // Sắp xếp lại danh sách tổng hợp theo thời gian mới nhất
        allMyNotifs.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
        return allMyNotifs;
    }
}