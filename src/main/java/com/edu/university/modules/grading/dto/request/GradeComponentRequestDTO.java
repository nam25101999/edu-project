package com.edu.university.modules.grading.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class GradeComponentRequestDTO {
    @NotNull(message = "ID lớp học phần không được để trống")
    private UUID courseSectionId;

    @NotBlank(message = "Mã thành phần điểm không được để trống")
    private String componentCode;

    @NotBlank(message = "Tên thành phần điểm không được để trống")
    private String componentName;

    @NotNull(message = "Trọng số không được để trống")
    private BigDecimal weightPercentage;

    private BigDecimal minScore;
    private BigDecimal maxScore;
    private boolean isRequired;
    private Integer inputOrder;
}
