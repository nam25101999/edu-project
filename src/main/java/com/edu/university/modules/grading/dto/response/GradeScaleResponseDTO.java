package com.edu.university.modules.grading.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GradeScaleResponseDTO {
    private UUID id;
    private String scaleCode;
    private BigDecimal minScore;
    private BigDecimal maxScore;
    private String letterGrade;
    private BigDecimal gpaValue;
    @com.fasterxml.jackson.annotation.JsonProperty("pass")
    private boolean pass;
    @com.fasterxml.jackson.annotation.JsonProperty("active")
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
