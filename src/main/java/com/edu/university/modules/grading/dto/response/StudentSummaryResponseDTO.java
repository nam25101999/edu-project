package com.edu.university.modules.grading.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StudentSummaryResponseDTO {
    private UUID id;
    private UUID registrationId;
    private String studentName;
    private String studentCode;
    private String courseName;
    private BigDecimal totalScore;
    private UUID scaleId;
    private String letterGrade;
    private BigDecimal gpaValue;
    private String result;
    private boolean isFinalized;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
