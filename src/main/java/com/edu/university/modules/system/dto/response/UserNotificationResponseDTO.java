package com.edu.university.modules.system.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserNotificationResponseDTO {
    private UUID id;
    private UUID userId;
    private UUID notificationId;
    private String notificationTitle;
    private boolean isRead;
    private LocalDateTime readAt;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
