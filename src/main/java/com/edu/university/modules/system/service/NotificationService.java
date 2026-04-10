package com.edu.university.modules.system.service;

import com.edu.university.modules.system.dto.request.NotificationRequestDTO;
import com.edu.university.modules.system.dto.response.NotificationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {
    NotificationResponseDTO create(NotificationRequestDTO requestDTO);
    Page<NotificationResponseDTO> getAll(Pageable pageable);
    void delete(UUID id);
}
