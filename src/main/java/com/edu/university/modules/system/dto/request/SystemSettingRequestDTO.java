package com.edu.university.modules.system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SystemSettingRequestDTO {
    @NotBlank(message = "Key không được để trống")
    private String key;

    @NotBlank(message = "Value không được để trống")
    private String value;

    private String description;
}
