package com.edu.university.modules.academic.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CourseSectionResponseDTO {
    private UUID id;
    private String classCode;
    private UUID courseId;
    private String courseName;
    private UUID semesterId;
    private String semesterName;
    private String academicYear;
    private UUID lecturerId;
    private String lecturerUsername;
    private UUID roomId;
    private UUID buildingId;
    private Integer maxStudents;
    private Integer minStudents;
    private String classType;
    private String status;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private String note;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
