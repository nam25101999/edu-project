package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentClassSectionRequestDTO {
    @NotNull(message = "ID sinh viên không được để trống")
    private UUID studentId;

    @NotNull(message = "ID lớp hành chính không được để trống")
    private UUID studentClassesId;

    private String status; // VD: "Đang học", "Đã chuyển lớp"
    private String note;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;
    private LocalDate endDate;
}