package com.edu.university.modules.system.controller;

import com.edu.university.modules.system.dto.request.UserNotificationRequestDTO;
import com.edu.university.modules.system.dto.response.UserNotificationResponseDTO;
import com.edu.university.modules.system.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    @PostMapping
    public ResponseEntity<UserNotificationResponseDTO> create(@RequestBody UserNotificationRequestDTO requestDTO) {
        return new ResponseEntity<>(userNotificationService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserNotificationResponseDTO>> getByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(userNotificationService.getByUserId(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        userNotificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
