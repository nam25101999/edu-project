package com.edu.university.modules.student.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AdvisorClassSectionResponseDTO {
    private UUID id;
    private UUID advisorId;
    private String advisorName; // Lấy từ bảng staff/user
    private UUID studentClassesId;
    private String className;   // Lấy từ bảng class
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}