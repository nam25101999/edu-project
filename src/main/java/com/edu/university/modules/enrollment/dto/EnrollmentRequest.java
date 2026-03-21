package com.edu.university.modules.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

// Tách riêng thành 1 file Record độc lập cho module Enrollment
public record EnrollmentRequest(
        @NotNull(message = "ID lớp học phần không được để trống")
        UUID classSectionId
) {}