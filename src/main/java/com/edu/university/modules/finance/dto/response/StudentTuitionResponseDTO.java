package com.edu.university.modules.finance.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StudentTuitionResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private UUID semesterId;
    private String semesterName;
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
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
