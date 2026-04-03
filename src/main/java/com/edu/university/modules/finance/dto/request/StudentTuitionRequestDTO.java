package com.edu.university.modules.finance.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentTuitionRequestDTO {
    @NotNull(message = "ID sinh viên không được để trống")
    private UUID studentId;

    @NotNull(message = "ID học kỳ không được để trống")
    private UUID semesterId;

    private UUID tuitionFeeId;
    private Integer totalCredits;
    private BigDecimal rawAmount;
    private BigDecimal scholarshipDeduction;
    private BigDecimal exemptionAmount;
    private BigDecimal netAmount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
    private Integer status;
    private LocalDate deadline;
}
