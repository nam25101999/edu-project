package com.edu.university.modules.curriculum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CourseRequestDTO {
    private UUID departmentId;

    @NotBlank(message = "Mã môn học không được để trống")
    private String courseCode;

    @NotBlank(message = "Tên môn học không được để trống")
    private String name;

    private String courseNameEn;

    @NotNull(message = "Số tín chỉ không được để trống")
    private BigDecimal credits;

    private String courseType;
    private BigDecimal theoryHours;
    private BigDecimal practiceHours;
    private BigDecimal selfStudyHours;
    private BigDecimal internshipCredits;
    private String description;
}
