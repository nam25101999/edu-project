package com.edu.university.modules.examination.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class ExamResponseDTO {
    private UUID id;
    private UUID examTypeId;
    private String examTypeName;
    private UUID courseClassId;
    private String courseClassName;
    private UUID semesterId;
    private String semesterName;
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private String examFormat;
    private String examStatus;
    private Integer supervisorCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
