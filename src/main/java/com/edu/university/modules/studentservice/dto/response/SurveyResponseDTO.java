package com.edu.university.modules.studentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private boolean isActive;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
