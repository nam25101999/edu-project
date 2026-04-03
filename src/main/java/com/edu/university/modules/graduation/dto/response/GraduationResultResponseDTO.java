package com.edu.university.modules.graduation.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GraduationResultResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private UUID conditionId;
    private BigDecimal gpa;
    private Integer totalCredits;
    private Integer failedCredits;
    private Integer result;
    private Integer classification;
    private LocalDate decisionDate;
    private UUID reviewerId;
    private String reviewerUsername;
    private String note;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
