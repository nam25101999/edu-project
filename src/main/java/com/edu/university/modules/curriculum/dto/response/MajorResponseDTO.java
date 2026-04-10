package com.edu.university.modules.curriculum.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MajorResponseDTO {
    private UUID id;
    private UUID facultyId;
    private String facultyName;
    private UUID departmentId;
    private String departmentName;
    private String majorCode;
    private String name;
    private String description;
    private String effectiveDate;
    private String expiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
