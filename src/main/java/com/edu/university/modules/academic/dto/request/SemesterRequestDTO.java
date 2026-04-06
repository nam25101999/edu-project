package com.edu.university.modules.academic.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SemesterRequestDTO {
    @NotBlank(message = "Mã học kỳ không được để trống")
    private String semesterCode;

    @NotBlank(message = "Tên học kỳ không được để trống")
    private String semesterName;

    @NotBlank(message = "Năm học không được để trống")
    private String academicYear;

    private LocalDate startDate;
    private LocalDate endDate;
}
