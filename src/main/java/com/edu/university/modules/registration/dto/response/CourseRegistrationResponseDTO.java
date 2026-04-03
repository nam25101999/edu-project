package com.edu.university.modules.registration.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CourseRegistrationResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private UUID courseSectionId;
    private String classCode;
    private String courseName;
    private UUID registrationPeriodId;
    private String registrationPeriodName;
    private Integer registrationType;
    private UUID replacedGradeId;
    private LocalDateTime registeredAt;
    private Integer status;
    private boolean isPaid;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
