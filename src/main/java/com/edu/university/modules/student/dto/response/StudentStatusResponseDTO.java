package com.edu.university.modules.student.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private LocalDateTime updatedAt;
}