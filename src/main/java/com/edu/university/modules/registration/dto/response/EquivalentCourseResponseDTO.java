package com.edu.university.modules.registration.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EquivalentCourseResponseDTO {
    private UUID id;
    private UUID originalCourseId;
    private String originalCourseName;
    private String originalCourseCode;
    private UUID equivalentCourseId;
    private String equivalentCourseName;
    private String equivalentCourseCode;
    private Integer equivalenceType;
    private LocalDate effectDate;
    private String note;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
