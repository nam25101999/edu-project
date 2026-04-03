package com.edu.university.modules.schedule.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BuildingResponseDTO {
    private UUID id;
    private String buildingCode;
    private String buildingName;
    private String address;
    private Integer totalFloors;
    private String buildingType;
    private String description;
    private String note;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
