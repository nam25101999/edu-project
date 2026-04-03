package com.edu.university.modules.system.service;

import com.edu.university.modules.system.dto.request.UserNotificationRequestDTO;
import com.edu.university.modules.system.dto.response.UserNotificationResponseDTO;

import java.util.List;
import java.util.UUID;

public interface UserNotificationService {
    UserNotificationResponseDTO create(UserNotificationRequestDTO requestDTO);
    List<UserNotificationResponseDTO> getByUserId(UUID userId);
    void markAsRead(UUID id);
}
