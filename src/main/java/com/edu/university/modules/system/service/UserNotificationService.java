package com.edu.university.modules.system.service;

import com.edu.university.modules.system.dto.request.UserNotificationRequestDTO;
import com.edu.university.modules.system.dto.response.UserNotificationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserNotificationService {
    UserNotificationResponseDTO create(UserNotificationRequestDTO requestDTO);
    Page<UserNotificationResponseDTO> getByUserId(UUID userId, Pageable pageable);
    void markAsRead(UUID id);
}
