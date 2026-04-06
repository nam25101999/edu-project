package com.edu.university.modules.curriculum.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrainingProgramCourseResponseDTO {
    private UUID id;
    private UUID trainingProgramId;
    private String programName;
    private UUID courseId;
    private String courseCode;
    private String courseName;
    private String semesterId;
    private String semesterCode;
    private String academicYear;
    private Boolean isRequired;
    private String groupCode;
    private BigDecimal credits;
    private UUID prerequisiteCourseId;
    private String prerequisiteCourseName;
    private Boolean isPrerequisiteRequired;
    private String note;
    private Integer sortOrder;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
