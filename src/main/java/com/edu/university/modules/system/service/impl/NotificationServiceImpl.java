package com.edu.university.modules.system.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.system.dto.request.NotificationRequestDTO;
import com.edu.university.modules.system.dto.response.NotificationResponseDTO;
import com.edu.university.modules.system.entity.Notification;
import com.edu.university.modules.system.mapper.NotificationMapper;
import com.edu.university.modules.system.repository.NotificationRepository;
import com.edu.university.modules.system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final RoleRepository roleRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public NotificationResponseDTO create(NotificationRequestDTO requestDTO) {
        Notification notification = notificationMapper.toEntity(requestDTO);
        
        if (requestDTO.getTargetRoleId() != null) {
            Role role = roleRepository.findById(requestDTO.getTargetRoleId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy vai trò đích"));
            notification.setTargetRole(role);
        }
        
        notification.setActive(true);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationMapper.toResponseDTO(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationResponseDTO> getAll() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông báo"));
        notification.softDelete("system");
        notificationRepository.save(notification);
    }
}
