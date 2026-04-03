package com.edu.university.modules.academic.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class LecturerCourseClassRequestDTO {
    @NotNull(message = "ID giảng viên không được để trống")
    private UUID lecturerId;

    @NotNull(message = "ID lớp học phần không được để trống")
    private UUID courseSectionId;

    @NotBlank(message = "Vai trò không được để trống")
    private String role;
}
