package com.edu.university.modules.grading.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StudentComponentGradeResponseDTO {
    private UUID id;
    private UUID registrationId;
    private String studentName;
    private String studentCode;
    private UUID componentId;
    private String componentName;
    private BigDecimal score;
    private boolean isRetake;
    private boolean isLocked;
    private LocalDateTime gradedAt;
    private UUID gradedById;
    private String gradedByUsername;
    private String note;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
