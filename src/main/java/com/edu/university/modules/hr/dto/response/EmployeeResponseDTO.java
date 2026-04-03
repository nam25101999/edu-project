package com.edu.university.modules.hr.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EmployeeResponseDTO {
    private UUID id;
    private UUID userId;
    private String employeeCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private UUID departmentId;
    private UUID positionId;
    private LocalDate hireDate;
    private String contractType;
    private BigDecimal salaryCoefficient;
    private String academicDegree;
    private String academicTitle;
    private String specialization;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
