package com.edu.university.modules.enrollment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GradeRequest(

        @NotNull(message = "Điểm chuyên cần không được để trống")
        @Min(value = 0, message = "Điểm phải >= 0")
        @Max(value = 10, message = "Điểm phải <= 10")
        Double attendance,

        @NotNull(message = "Điểm giữa kỳ không được để trống")
        @Min(value = 0, message = "Điểm phải >= 0")
        @Max(value = 10, message = "Điểm phải <= 10")
        Double midterm,

        @NotNull(message = "Điểm cuối kỳ không được để trống")
        @Min(value = 0, message = "Điểm phải >= 0")
        @Max(value = 10, message = "Điểm phải <= 10")
        Double finalScore
) {}