package com.edu.university.modules.enrollment.repository.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ClassSectionDtos {

    public record ClassSectionRequest(
            @NotNull(message = "ID môn học không được để trống")
            UUID courseId,

            // Có thể null nếu lớp chưa được phân công giảng viên
            UUID lecturerId,

            @NotBlank(message = "Học kỳ không được để trống")
            String semester,

            @NotNull(message = "Năm học không được để trống")
            Integer year,

            @NotBlank(message = "Lịch học không được để trống (VD: T2, 1-3)")
            String schedule,

            @NotBlank(message = "Phòng học không được để trống")
            String room,

            @NotNull(message = "Số lượng sinh viên tối đa không được để trống")
            Integer maxStudents
    ) {}
}