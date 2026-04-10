package com.edu.university.modules.system.service.impl;

import com.edu.university.modules.system.dto.response.SystemLogResponseDTO;
import com.edu.university.modules.system.mapper.SystemLogMapper;
import com.edu.university.modules.system.repository.SystemLogRepository;
import com.edu.university.modules.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogRepository systemLogRepository;
    private final SystemLogMapper systemLogMapper;

    @Override
    public Page<SystemLogResponseDTO> getAll(Pageable pageable) {
        return systemLogRepository.findAll(pageable)
                .map(systemLogMapper::toResponseDTO);
    }

    @Override
    public Page<SystemLogResponseDTO> getByUserId(UUID userId, Pageable pageable) {
        return systemLogRepository.findByUserId(userId, pageable)
                .map(systemLogMapper::toResponseDTO);
    }
}
