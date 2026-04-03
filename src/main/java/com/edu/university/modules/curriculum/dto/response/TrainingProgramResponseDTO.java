package com.edu.university.modules.curriculum.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrainingProgramResponseDTO {
    private UUID id;
    private String programCode;
    private String programName;
    private String programNameEn;
    private UUID majorId;
    private String majorName;
    private UUID departmentId;
    private String departmentName;
    private String degreeLevel;
    private String educationType;
    private BigDecimal totalCredits;
    private BigDecimal requiredCredits;
    private BigDecimal electiveCredits;
    private BigDecimal internshipCredits;
    private BigDecimal thesisCredits;
    private LocalDate admissionYear;
    private BigDecimal durationYears;
    private BigDecimal maxDurationYears;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String description;
    private String objectives;
    private String learningOutcomes;
    private String version;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
