package com.edu.university.modules.academic.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AcademicYearResponseDTO {
    private UUID id;
    private String academicCode;
    private String academicName;
    private String academicYear;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
