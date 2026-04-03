package com.edu.university.modules.curriculum.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MajorResponseDTO {
    private UUID id;
    private UUID facultyId;
    private String facultyName;
    private String code;
    private String name;
    private String description;
    private String effectiveDate;
    private String expiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
