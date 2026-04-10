package com.edu.university.modules.student.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StudentClassResponseDTO {
    private UUID id;
    private String classCode;
    private String className;
    private UUID departmentId;
    private String departmentName; // Bổ sung khi query (Join)
    private UUID majorId;
    private String majorName;      // Bổ sung khi query (Join)
    private String academicYear;
    private Boolean isActive;
    private UUID advisorId;
    private String advisorName;
    private LocalDateTime createdAt;
}