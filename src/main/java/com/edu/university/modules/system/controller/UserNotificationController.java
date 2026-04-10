package com.edu.university.modules.system.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.system.dto.request.UserNotificationRequestDTO;
import com.edu.university.modules.system.dto.response.UserNotificationResponseDTO;
import com.edu.university.modules.system.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    @PostMapping
    public ResponseEntity<BaseResponse<UserNotificationResponseDTO>> create(@RequestBody UserNotificationRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.created(userNotificationService.create(requestDTO)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<PageResponse<UserNotificationResponseDTO>>> getByUserId(@PathVariable UUID userId, @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(userNotificationService.getByUserId(userId, pageable)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<BaseResponse<Void>> markAsRead(@PathVariable UUID id) {
        userNotificationService.markAsRead(id);
        return ResponseEntity.ok(BaseResponse.ok("Đã đánh dấu là đã đọc", null));
    }
}
