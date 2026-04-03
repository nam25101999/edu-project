package com.edu.university.modules.curriculum.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TrainingProgramRequestDTO {
    @NotBlank(message = "Mã chương trình không được để trống")
    private String programCode;

    @NotBlank(message = "Tên chương trình không được để trống")
    private String programName;

    private String programNameEn;
    private UUID majorId;
    private UUID departmentId;
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
}
