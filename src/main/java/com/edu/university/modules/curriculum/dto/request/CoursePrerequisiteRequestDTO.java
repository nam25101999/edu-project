package com.edu.university.modules.curriculum.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class CoursePrerequisiteRequestDTO {
    @NotNull(message = "ID môn học không được để trống")
    private UUID courseId;

    @NotNull(message = "ID môn tiên quyết không được để trống")
    private UUID prerequisiteCourseId;
}
