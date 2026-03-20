package com.edu.university.dto;

import com.edu.university.entity.ExamType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class ExamScheduleDtos {

    public record ExamScheduleRequest(
            @NotNull(message = "ID lớp học phần không được để trống")
            UUID classSectionId,

            @NotNull(message = "Loại kỳ thi không được để trống")
            ExamType examType,

            @NotNull(message = "Thời gian bắt đầu không được để trống")
            LocalDateTime startTime,

            @NotNull(message = "Thời gian kết thúc không được để trống")
            LocalDateTime endTime,

            @NotBlank(message = "Phòng thi không được để trống")
            String room
    ) {}
}