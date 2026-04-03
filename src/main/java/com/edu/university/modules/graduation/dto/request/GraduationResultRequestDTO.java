package com.edu.university.modules.graduation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class GraduationResultRequestDTO {
    @NotNull(message = "ID sinh viên không được để trống")
    private UUID studentId;

    @NotNull(message = "ID điều kiện tốt nghiệp không được để trống")
    private UUID conditionId;

    private BigDecimal gpa;
    private Integer totalCredits;
    private Integer failedCredits;
    private Integer result;
    private Integer classification;
    private LocalDate decisionDate;
    private UUID reviewerId;
    private String note;
}
