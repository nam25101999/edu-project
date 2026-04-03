package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Dành riêng cho API PATCH /api/students/{id}/status
@Data
public class StudentStatusChangeRequestDTO {
    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive;
}