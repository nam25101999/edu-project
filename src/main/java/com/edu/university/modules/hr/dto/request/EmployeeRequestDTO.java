package com.edu.university.modules.hr.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class EmployeeRequestDTO {
    private UUID userId;

    @NotBlank(message = "Mã nhân viên không được để trống")
    private String employeeCode;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    private LocalDate dateOfBirth;
    private String gender; // 1: Nam, 2: Nữ, 0: Khác

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String phone;
    private String address;

    private UUID departmentId;
    private Set<UUID> positionIds;

    private LocalDate hireDate;
    private String contractType;
    private BigDecimal salaryCoefficient;
    private String academicDegree;
    private String academicTitle;
    private String specialization;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
