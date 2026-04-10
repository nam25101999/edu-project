package com.edu.university.modules.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponseDTO {
    private UUID id;
    private String employeeCode;
    private String fullName;
    private String email;
    private String phone;
    private UUID userId;
    private UUID departmentId;
    private String departmentName;
    private Set<PositionResponseDTO> positions;
    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
