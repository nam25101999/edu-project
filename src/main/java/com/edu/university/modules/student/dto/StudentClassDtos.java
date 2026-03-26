package com.edu.university.modules.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class StudentClassDtos {

    public record StudentClassRequest(
            @NotBlank(message = "Mã lớp không được để trống") String classCode,
            @NotBlank(message = "Tên lớp không được để trống") String name,
            @NotNull(message = "ID của Ngành học không được để trống") UUID majorId
    ) {}

    // DTO để trả dữ liệu ra client
    public record StudentClassResponse(
            UUID id,
            String classCode,
            String name,
            UUID majorId,
            String majorName // Nếu muốn hiển thị tên ngành học
    ) {}
}