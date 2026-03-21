package com.edu.university.modules.enrollment.dto;

import jakarta.validation.constraints.NotNull;

// Tách riêng thành 1 file Record độc lập cho module Grade
public record GradeRequest(
        @NotNull(message = "Điểm chuyên cần không được để trống")
        Double attendance,

        @NotNull(message = "Điểm giữa kỳ không được để trống")
        Double midterm,

        @NotNull(message = "Điểm cuối kỳ không được để trống")
        Double finalScore
) {}