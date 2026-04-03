package com.edu.university.modules.student.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StudentStatusResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentCode;
    private String studentName;
    private String status;
    private String reason;
    private String note;
    private LocalDate effectiveDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
}