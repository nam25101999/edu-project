package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class StudentClassRequestDTO {
    @NotBlank(message = "Mã lớp không được để trống")
    private String classCode;

    @NotBlank(message = "Tên lớp không được để trống")
    private String className;

    @NotNull(message = "ID Khoa không được để trống")
    private UUID departmentId;

    @NotNull(message = "ID Ngành không được để trống")
    private UUID majorId;

    @NotBlank(message = "Niên khóa không được để trống")
    private String academicYear; // VD: 2020-2024

    private UUID advisorId;
}