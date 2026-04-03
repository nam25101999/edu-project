package com.edu.university.modules.system.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserNotificationRequestDTO {
    private UUID userId;
    private UUID notificationId;
    private boolean isRead;
    private LocalDateTime readAt;
}
