package com.edu.university.modules.academic.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StudentCourseSectionResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private UUID courseSectionId;
    private String classCode;
    private String status;
    private LocalDateTime registeredAt;
    private String note;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
