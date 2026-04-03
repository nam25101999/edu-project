package com.edu.university.modules.student.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class StudentClassSectionResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentCode; // Lấy từ bảng students
    private String studentName; // Lấy từ bảng students
    private UUID studentClassesId;
    private String className;   // Lấy từ bảng student_classes
    private String status;
    private String note;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}