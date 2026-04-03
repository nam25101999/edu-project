package com.edu.university.modules.graduation.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GraduationConditionResponseDTO {
    private UUID id;
    private UUID trainingProgramId;
    private String trainingProgramName;
    private String appliedCohort;
    private Integer minTotalCredits;
    private BigDecimal minGpa;
    private Integer maxFailedCredits;
    private String englishRequirement;
    private String itRequirement;
    private String conductRequired;
    private String note;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
