package com.edu.university.modules.schedule.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RoomResponseDTO {
    private UUID id;
    private String roomCode;
    private String roomName;
    private UUID buildingId;
    private String buildingName;
    private Integer floor;
    private Integer capacity;
    private String roomType;
    private String status;
    private boolean hasProjector;
    private boolean hasAirConditioner;
    private boolean hasComputer;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
