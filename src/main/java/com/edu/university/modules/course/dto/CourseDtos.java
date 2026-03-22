package com.edu.university.modules.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CourseDtos {

    public record CourseRequest(
            @NotBlank(message = "Mã môn học không được để trống")
            String courseCode,

            @NotBlank(message = "Tên môn học không được để trống")
            String name,

            @NotNull(message = "Số tín chỉ không được để trống")
            Integer credits,

            UUID prerequisiteCourseId
    ) {}
}