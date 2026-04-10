package com.edu.university.modules.studentservice.mapper;

import com.edu.university.modules.studentservice.dto.response.SurveyResponseDTO;
import com.edu.university.modules.studentservice.dto.response.SurveyResultResponseDTO;
import com.edu.university.modules.studentservice.entity.Survey;
import com.edu.university.modules.studentservice.entity.SurveyResponse;
import org.springframework.stereotype.Component;

@Component
public class SurveyMapper {

    public SurveyResponseDTO toResponseDTO(Survey survey) {
        if (survey == null) return null;
        return SurveyResponseDTO.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .isActive(survey.isActive())
                .startTime(survey.getStartTime())
                .endTime(survey.getEndTime())
                .build();
    }

    public SurveyResultResponseDTO toResultResponseDTO(SurveyResponse response) {
        if (response == null) return null;
        return SurveyResultResponseDTO.builder()
                .id(response.getId())
                .surveyId(response.getSurvey() != null ? response.getSurvey().getId() : null)
                .surveyTitle(response.getSurvey() != null ? response.getSurvey().getTitle() : null)
                .studentId(response.getStudent() != null ? response.getStudent().getId() : null)
                .studentName(response.getStudent() != null ? response.getStudent().getFullName() : null)
                .answersJson(response.getAnswersJson())
                .submittedAt(response.getSubmittedAt())
                .build();
    }
}
