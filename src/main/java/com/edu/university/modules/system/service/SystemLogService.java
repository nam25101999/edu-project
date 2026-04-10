package com.edu.university.modules.system.service;

import com.edu.university.modules.system.dto.response.SystemLogResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SystemLogService {
    Page<SystemLogResponseDTO> getAll(Pageable pageable);
    Page<SystemLogResponseDTO> getByUserId(UUID userId, Pageable pageable);
}
