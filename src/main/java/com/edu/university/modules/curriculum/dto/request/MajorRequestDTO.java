package com.edu.university.modules.curriculum.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class MajorRequestDTO {
    private UUID facultyId;
    private UUID departmentId;

    @NotBlank(message = "Mã ngành không được để trống")
    private String majorCode;

    @NotBlank(message = "Tên ngành không được để trống")
    private String name;

    private String description;
    private String effectiveDate;
    private String expiryDate;
}
