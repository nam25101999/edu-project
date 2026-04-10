package com.edu.university.modules.finance.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID semesterId;
    private String semesterName;
    private String name;
    private BigDecimal amount;
    private String status;
}
