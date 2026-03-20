package com.edu.university.controller;

import com.edu.university.dto.NotificationDtos.NotificationRequest;
import com.edu.university.security.UserDetailsImpl;
import com.edu.university.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ADMIN gửi thông báo cho toàn trường (Mở đăng ký, Lịch nghỉ Tết...)
    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendToAll(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendToAll(request.title(), request.message()));
    }

    // ADMIN hoặc GIẢNG VIÊN gửi thông báo cho 1 lớp học phần (Nghỉ học, Dời phòng thi...)
    @PostMapping("/class")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<?> sendToClass(@Valid @RequestBody NotificationRequest request) {
        if (request.targetId() == null) {
            return ResponseEntity.badRequest().body("Cần cung cấp targetId là ID của lớp học phần.");
        }
        return ResponseEntity.ok(notificationService.sendToClass(request.targetId(), request.title(), request.message()));
    }

    // Gửi thông báo cho cá nhân (Nhắc nhở học phí, Cảnh báo học vụ...)
    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendToUser(@Valid @RequestBody NotificationRequest request) {
        if (request.targetId() == null) {
            return ResponseEntity.badRequest().body("Cần cung cấp targetId là ID của User.");
        }
        return ResponseEntity.ok(notificationService.sendToUser(request.targetId(), request.title(), request.message()));
    }

    // User (Bất kỳ Role nào) xem hộp thư thông báo của mình
    @GetMapping("/my")
    public ResponseEntity<?> getMyNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userDetails.getId()));
    }
}