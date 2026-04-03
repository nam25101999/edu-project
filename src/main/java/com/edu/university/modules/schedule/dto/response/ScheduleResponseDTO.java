package com.edu.university.modules.schedule.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ScheduleResponseDTO {
    private UUID id;
    private UUID courseSectionId;
    private String classCode;
    private UUID lecturerId;
    private String lecturerUsername;
    private UUID roomId;
    private String roomName;
    private Integer dayOfWeek;
    private LocalDate date;
    private String shift;
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer numberOfPeriods;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String mode;
    private String status;
    private String description;
    private String scheduleStatus;
    private String note;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
