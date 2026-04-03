package com.edu.university.modules.grading.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class StudentSummaryRequestDTO {
    @NotNull(message = "ID đăng ký học phần không được để trống")
    private UUID registrationId;

    private BigDecimal totalScore;
    private UUID scaleId;
    private String letterGrade;
    private BigDecimal gpaValue;
    private String result;
    private boolean isFinalized;
}
