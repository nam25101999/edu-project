package com.edu.university.modules.finance.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentRequestDTO {
    @NotNull(message = "ID học phí sinh viên không được để trống")
    private UUID studentTuitionId;

    private BigDecimal amountPaid;
    private LocalDateTime paymentDate;
    private Integer paymentMethod;
    private String paymentStatus;
    private String transactionRef;
    private UUID cashierId;
    private String notes;
}
