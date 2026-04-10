package com.edu.university.modules.hr.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DepartmentRequestDTO {
    @NotBlank(message = "Mã phòng ban/khoa không được để trống")
    private String code;

    @NotBlank(message = "Tên phòng ban/khoa không được để trống")
    private String name;

    private String description;
    
    private LocalDate establishedDate;
}
