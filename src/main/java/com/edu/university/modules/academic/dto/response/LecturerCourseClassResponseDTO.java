package com.edu.university.modules.academic.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LecturerCourseClassResponseDTO {
    private UUID id;
    private UUID lecturerId;
    private String lecturerUsername;
    private UUID courseSectionId;
    private String classCode;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
