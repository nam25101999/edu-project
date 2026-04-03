package com.edu.university.modules.registration.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RegistrationPeriodResponseDTO {
    private UUID id;
    private String name;
    private UUID semesterId;
    private String semesterName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String targetConfig;
    private Integer maxCredits;
    private Integer minCredits;
    private boolean allowRetake;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
