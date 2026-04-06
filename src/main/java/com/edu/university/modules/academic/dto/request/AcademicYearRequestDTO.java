package com.edu.university.modules.academic.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcademicYearRequestDTO {
    @NotBlank(message = "Mã năm học không được để trống")
    private String academicCode;

    @NotBlank(message = "Tên năm học không được để trống")
    private String academicName;

    @NotBlank(message = "Năm học không được để trống (VD: 2023-2024)")
    private String academicYear;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
