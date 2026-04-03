package com.edu.university.modules.examination.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExamResultResponseDTO {
    private UUID id;
    private UUID registrationId;
    private String studentName;
    private String studentCode;
    private BigDecimal score;
    private String status;
    private UUID gradedById;
    private String gradedByUsername;
    private LocalDateTime gradedAt;
    private boolean isLocked;
    private String appealStatus;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
