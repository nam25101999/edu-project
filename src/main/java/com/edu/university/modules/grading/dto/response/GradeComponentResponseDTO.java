package com.edu.university.modules.grading.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GradeComponentResponseDTO {
    private UUID id;
    private UUID courseSectionId;
    private String classCode;
    private String componentCode;
    private String componentName;
    private BigDecimal weightPercentage;
    private BigDecimal minScore;
    private BigDecimal maxScore;
    private boolean isRequired;
    private Integer inputOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
