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
public class SemesterResponseDTO {
    private UUID id;
    private String semesterCode;
    private String semesterName;
    private String academicYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
