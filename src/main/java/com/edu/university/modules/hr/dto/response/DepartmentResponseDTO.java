package com.edu.university.modules.hr.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DepartmentResponseDTO {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private LocalDate establishedYear;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
