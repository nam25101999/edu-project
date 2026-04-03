package com.edu.university.modules.student.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StudentResponseDTO {
    private UUID id;
    private String studentCode;
    private UUID userId;
    private UUID departmentId;
    private UUID majorId;
    private UUID programId;
    private String firstName;
    private String lastName;
    private String fullName; // Có thể map: firstName + " " + lastName
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private Integer gender;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}