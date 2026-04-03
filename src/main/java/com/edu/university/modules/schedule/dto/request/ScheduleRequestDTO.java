package com.edu.university.modules.schedule.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ScheduleRequestDTO {
    private UUID courseSectionId;
    private UUID lecturerId;
    private UUID roomId;
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
}
