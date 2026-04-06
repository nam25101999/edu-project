package com.edu.university.modules.academic.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
