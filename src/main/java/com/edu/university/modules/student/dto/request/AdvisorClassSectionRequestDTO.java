package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class AdvisorClassSectionRequestDTO {
    @NotNull(message = "ID cố vấn không được để trống")
    private UUID advisorId; // Có thể map với bảng users hoặc staffs

    @NotNull(message = "ID lớp hành chính không được để trống")
    private UUID studentClassesId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    private LocalDate endDate;
}