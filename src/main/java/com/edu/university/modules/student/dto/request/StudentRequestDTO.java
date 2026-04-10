package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentRequestDTO {
    @NotBlank(message = "Ma sinh vien khong duoc de trong")
    private String studentCode;

    private UUID userId;
    private UUID studentClassId;

    private UUID departmentId;
    private UUID majorId;
    private UUID programId;
    private String classCode;

    @NotBlank(message = "Ho dem khong duoc de trong")
    private String firstName;

    @NotBlank(message = "Ten khong duoc de trong")
    private String lastName;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong dung dinh dang")
    private String email;

    private String phone;
    private LocalDate dateOfBirth;
    private Integer gender;
    private String address;
}
