package com.edu.university.modules.schedule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentScheduleResponseDTO {
    private String courseName;
    private String classCode;
    private String roomName;
    private String buildingName;
    private String lecturerName;
    private Integer dayOfWeek;
    private LocalDate date;
    private String shift;
    private Integer startPeriod;
    private Integer endPeriod;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
