package com.edu.university.modules.system.service;

import com.edu.university.modules.system.dto.request.SystemSettingRequestDTO;
import com.edu.university.modules.system.dto.response.SystemSettingResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SystemSettingService {
    SystemSettingResponseDTO update(SystemSettingRequestDTO requestDTO);
    Page<SystemSettingResponseDTO> getAll(Pageable pageable);
    SystemSettingResponseDTO getByKey(String key);
}
