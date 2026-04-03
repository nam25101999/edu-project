package com.edu.university.modules.hr.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeStatusChangeRequestDTO {
    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive;
}
