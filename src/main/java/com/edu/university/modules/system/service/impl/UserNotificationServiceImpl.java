package com.edu.university.modules.system.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.system.dto.request.UserNotificationRequestDTO;
import com.edu.university.modules.system.dto.response.UserNotificationResponseDTO;
import com.edu.university.modules.system.entity.Notification;
import com.edu.university.modules.system.entity.UserNotification;
import com.edu.university.modules.system.mapper.UserNotificationMapper;
import com.edu.university.modules.system.repository.NotificationRepository;
import com.edu.university.modules.system.repository.UserNotificationRepository;
import com.edu.university.modules.system.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationMapper userNotificationMapper;

    @Override
    @Transactional
    public UserNotificationResponseDTO create(UserNotificationRequestDTO requestDTO) {
        UserNotification un = userNotificationMapper.toEntity(requestDTO);
        
        Users user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy người dùng"));
        Notification n = notificationRepository.findById(requestDTO.getNotificationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông báo"));
        
        un.setUser(user);
        un.setNotification(n);
        un.setActive(true);
        un.setCreatedAt(LocalDateTime.now());
        return userNotificationMapper.toResponseDTO(userNotificationRepository.save(un));
    }

    @Override
    public List<UserNotificationResponseDTO> getByUserId(UUID userId) {
        return userNotificationRepository.findByUserId(userId).stream()
                .map(userNotificationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(UUID id) {
        UserNotification un = userNotificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông báo người dùng"));
        un.setRead(true);
        un.setReadAt(LocalDateTime.now());
        userNotificationRepository.save(un);
    }
}
