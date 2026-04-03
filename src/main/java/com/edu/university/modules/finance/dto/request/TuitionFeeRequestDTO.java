package com.edu.university.modules.finance.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class TuitionFeeRequestDTO {
    @NotNull(message = "ID chương trình đào tạo không được để trống")
    private UUID trainingProgramId;

    private String courseYear;
    private BigDecimal pricePerCredit;
    private BigDecimal baseTuition;
    private LocalDate effectiveDate;
}
