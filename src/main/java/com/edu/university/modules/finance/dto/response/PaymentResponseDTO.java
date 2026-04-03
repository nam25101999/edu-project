package com.edu.university.modules.finance.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PaymentResponseDTO {
    private UUID id;
    private UUID studentTuitionId;
    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private Integer paymentMethod;
    private String paymentStatus;
    private String transactionRef;
    private UUID cashierId;
    private String cashierUsername;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
