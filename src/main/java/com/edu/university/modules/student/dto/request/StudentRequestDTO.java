package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentRequestDTO {
    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    @NotNull(message = "User ID không được để trống")
    private UUID userId;

    private UUID departmentId; // Khoa
    private UUID majorId;      // Ngành
    private UUID programId;    // Chương trình học

    @NotBlank(message = "Họ đệm không được để trống")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String phone;
    private LocalDate dateOfBirth;
    private Integer gender; // 0: Nữ, 1: Nam, 2: Khác
    private String address;
}