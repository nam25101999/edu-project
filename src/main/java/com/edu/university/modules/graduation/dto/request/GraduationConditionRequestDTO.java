package com.edu.university.modules.graduation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class GraduationConditionRequestDTO {
    @NotNull(message = "ID chương trình đào tạo không được để trống")
    private UUID trainingProgramId;

    @NotBlank(message = "Khóa áp dụng không được để trống")
    private String appliedCohort;

    private Integer minTotalCredits;
    private BigDecimal minGpa;
    private Integer maxFailedCredits;
    private String englishRequirement;
    private String itRequirement;
    private String conductRequired;
    private String note;
}
