package com.edu.university.modules.system.service;

import com.edu.university.modules.system.dto.request.SystemSettingRequestDTO;
import com.edu.university.modules.system.dto.response.SystemSettingResponseDTO;

import java.util.List;

public interface SystemSettingService {
    SystemSettingResponseDTO update(SystemSettingRequestDTO requestDTO);
    List<SystemSettingResponseDTO> getAll();
    SystemSettingResponseDTO getByKey(String key);
}
