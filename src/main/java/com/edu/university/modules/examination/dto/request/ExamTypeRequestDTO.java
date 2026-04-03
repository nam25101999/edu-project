package com.edu.university.modules.examination.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExamTypeRequestDTO {
    @NotBlank(message = "Tên loại kỳ thi không được để trống")
    private String name;
    private String description;
}
