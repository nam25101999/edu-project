package com.edu.university.modules.system.service.impl;

import com.edu.university.modules.system.dto.response.SystemLogResponseDTO;
import com.edu.university.modules.system.mapper.SystemLogMapper;
import com.edu.university.modules.system.repository.SystemLogRepository;
import com.edu.university.modules.system.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogRepository systemLogRepository;
    private final SystemLogMapper systemLogMapper;

    @Override
    public List<SystemLogResponseDTO> getAll() {
        return systemLogRepository.findAll().stream()
                .map(systemLogMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogResponseDTO> getByUserId(UUID userId) {
        return systemLogRepository.findByUserId(userId).stream()
                .map(systemLogMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
