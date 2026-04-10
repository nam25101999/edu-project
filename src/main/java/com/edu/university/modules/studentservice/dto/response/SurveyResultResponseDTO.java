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
public class SurveyResultResponseDTO {
    private UUID id;
    private UUID surveyId;
    private String surveyTitle;
    private UUID studentId;
    private String studentName;
    private String answersJson;
    private LocalDateTime submittedAt;
}
