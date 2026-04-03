package com.edu.university.modules.grading.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class GradeScaleRequestDTO {
    @NotBlank(message = "Mã thang điểm không được để trống")
    private String scaleCode;

    @NotNull(message = "Điểm tối thiểu không được để trống")
    private BigDecimal minScore;

    @NotNull(message = "Điểm tối đa không được để trống")
    private BigDecimal maxScore;

    @NotBlank(message = "Điểm chữ không được để trống")
    private String letterGrade;

    @NotNull(message = "Giá trị GPA không được để trống")
    private BigDecimal gpaValue;

    private boolean isPass;
}
