package com.edu.university.modules.examination.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class ExamRequestDTO {
    @NotNull(message = "ID loại kỳ thi không được để trống")
    private UUID examTypeId;

    @NotNull(message = "ID lớp học phần không được để trống")
    private UUID courseClassId;

    @NotNull(message = "ID học kỳ không được để trống")
    private UUID semesterId;

    private LocalDate examDate;
    private LocalTime startTime;
    private Integer durationMinutes;
    private String examFormat;
    private String examStatus;
    private Integer supervisorCount;
}
