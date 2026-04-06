package com.edu.university.modules.schedule.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeSlotResponseDTO {
    private UUID id;
    private String slotCode;
    private String slotName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
