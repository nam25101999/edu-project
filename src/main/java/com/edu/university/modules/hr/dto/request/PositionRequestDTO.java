package com.edu.university.modules.hr.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class PositionRequestDTO {
    @NotBlank(message = "Mã chức vụ không được để trống")
    private String code;

    @NotBlank(message = "Tên chức vụ không được để trống")
    private String name;

    private String description;
    
    private String level;

    @NotNull(message = "ID phòng ban/khoa không được để trống")
    private UUID departmentId;
}
