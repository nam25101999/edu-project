package com.edu.university.modules.system.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SystemLogResponseDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private String action;
    private String tableName;
    private UUID recordId;
    private String description;
    private LocalDateTime createdAt;
}
