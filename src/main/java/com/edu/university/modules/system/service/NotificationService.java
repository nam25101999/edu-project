package com.edu.university.modules.system.service;

import com.edu.university.modules.system.dto.request.NotificationRequestDTO;
import com.edu.university.modules.system.dto.response.NotificationResponseDTO;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationResponseDTO create(NotificationRequestDTO requestDTO);
    List<NotificationResponseDTO> getAll();
    void delete(UUID id);
}
