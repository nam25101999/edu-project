package com.edu.university.modules.curriculum.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
