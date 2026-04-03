package com.edu.university.modules.curriculum.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CoursePrerequisiteResponseDTO {
    private UUID id;
    private UUID courseId;
    private String courseName;
    private UUID prerequisiteCourseId;
    private String prerequisiteCourseName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
