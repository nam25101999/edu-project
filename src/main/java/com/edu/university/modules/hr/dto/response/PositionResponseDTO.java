package com.edu.university.modules.hr.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PositionResponseDTO {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String level;
    private UUID departmentId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
