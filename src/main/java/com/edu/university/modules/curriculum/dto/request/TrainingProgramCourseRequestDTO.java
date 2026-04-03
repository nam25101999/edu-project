package com.edu.university.modules.curriculum.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TrainingProgramCourseRequestDTO {
    private UUID trainingProgramId;
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
    private Boolean isPrerequisiteRequired;
    private String note;
    private Integer sortOrder;
    private String status;
}
