package com.edu.university.modules.curriculum.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CourseResponseDTO {
    private UUID id;
    private UUID departmentId;
    private String departmentName;
    private String code;
    private String name;
    private String courseNameEn;
    private BigDecimal credits;
    private String courseType;
    private BigDecimal theoryHours;
    private BigDecimal practiceHours;
    private BigDecimal selfStudyHours;
    private BigDecimal internshipCredits;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
