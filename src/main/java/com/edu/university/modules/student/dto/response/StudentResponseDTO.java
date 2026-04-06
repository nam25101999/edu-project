package com.edu.university.modules.student.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponseDTO {
    private UUID id;
    private String studentCode;
    private UUID userId;
    private UUID departmentId;
    private String departmentName;
    private UUID majorId;
    private String majorName;
    private UUID programId;
    private String programName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Integer gender;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}