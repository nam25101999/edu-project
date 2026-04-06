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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    @Mock
    private UserNotificationRepository userNotificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserNotificationMapper userNotificationMapper;

    @InjectMocks
    private UserNotificationServiceImpl userNotificationService;

    private UserNotification un;
    private UserNotificationRequestDTO requestDTO;
    private UserNotificationResponseDTO responseDTO;
    private UUID unId;
    private UUID userId;
    private UUID notificationId;

    @BeforeEach
    void setUp() {
        unId = UUID.randomUUID();
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        un = new UserNotification();
        un.setId(unId);

        requestDTO = new UserNotificationRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setNotificationId(notificationId);

        responseDTO = UserNotificationResponseDTO.builder()
                .id(unId)
                .userId(userId)
                .notificationId(notificationId)
                .isRead(false)
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(userNotificationMapper.toEntity(any())).thenReturn(un);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new Users()));
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(new Notification()));
        when(userNotificationRepository.save(any())).thenReturn(un);
        when(userNotificationMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        UserNotificationResponseDTO result = userNotificationService.create(requestDTO);

        // Assert
        assertNotNull(result);
        verify(userNotificationRepository).save(any());
    }

    @Test
    void create_UserNotFound() {
        // Arrange
        when(userNotificationMapper.toEntity(any())).thenReturn(un);
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userNotificationService.create(requestDTO));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getByUserId_Success() {
        // Arrange
        when(userNotificationRepository.findByUserId(userId)).thenReturn(Collections.singletonList(un));
        when(userNotificationMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        List<UserNotificationResponseDTO> results = userNotificationService.getByUserId(userId);

        // Assert
        assertEquals(1, results.size());
    }

    @Test
    void markAsRead_Success() {
        // Arrange
        when(userNotificationRepository.findById(unId)).thenReturn(Optional.of(un));

        // Act
        userNotificationService.markAsRead(unId);

        // Assert
        assertTrue(un.isRead());
        assertNotNull(un.getReadAt());
        verify(userNotificationRepository).save(un);
    }

    @Test
    void markAsRead_NotFound() {
        // Arrange
        when(userNotificationRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> userNotificationService.markAsRead(unId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }
}
