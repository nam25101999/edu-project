package com.edu.university.modules.system.service;

import com.edu.university.modules.system.dto.response.SystemLogResponseDTO;

import java.util.List;
import java.util.UUID;

public interface SystemLogService {
    List<SystemLogResponseDTO> getAll();
    List<SystemLogResponseDTO> getByUserId(UUID userId);
}
