package com.edu.university.modules.system.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.system.dto.request.SystemSettingRequestDTO;
import com.edu.university.modules.system.dto.response.SystemSettingResponseDTO;
import com.edu.university.modules.system.entity.SystemSetting;
import com.edu.university.modules.system.mapper.SystemSettingMapper;
import com.edu.university.modules.system.repository.SystemSettingRepository;
import com.edu.university.modules.system.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;
    private final SystemSettingMapper systemSettingMapper;

    @Override
    @Transactional
    public SystemSettingResponseDTO update(SystemSettingRequestDTO requestDTO) {
        SystemSetting setting = systemSettingRepository.findByKey(requestDTO.getKey())
                .orElse(new SystemSetting());
        
        systemSettingMapper.updateEntityFromDTO(requestDTO, setting);
        
        if (setting.getId() == null) {
            setting.setActive(true);
            setting.setCreatedAt(LocalDateTime.now());
        } else {
            setting.setUpdatedAt(LocalDateTime.now());
        }
        
        return systemSettingMapper.toResponseDTO(systemSettingRepository.save(setting));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemSettingResponseDTO> getAll(Pageable pageable) {
        return systemSettingRepository.findAll(pageable)
                .map(systemSettingMapper::toResponseDTO);
    }

    @Override
    public SystemSettingResponseDTO getByKey(String key) {
        SystemSetting setting = systemSettingRepository.findByKey(key)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy cấu hình hệ thống: " + key));
        return systemSettingMapper.toResponseDTO(setting);
    }
}
