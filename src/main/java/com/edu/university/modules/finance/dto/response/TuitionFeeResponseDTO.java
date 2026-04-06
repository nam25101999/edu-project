package com.edu.university.modules.finance.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TuitionFeeResponseDTO {
    private UUID id;
    private UUID trainingProgramId;
    private String trainingProgramName;
    private String courseYear;
    private BigDecimal pricePerCredit;
    private BigDecimal baseTuition;
    private LocalDate effectiveDate;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
