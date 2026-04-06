package com.edu.university.modules.studentservice.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.entity.Survey;
import com.edu.university.modules.studentservice.entity.SurveyResponse;
import com.edu.university.modules.studentservice.repository.SurveyRepository;
import com.edu.university.modules.studentservice.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final StudentRepository studentRepository;

    public List<Survey> getActiveSurveys() {
        return surveyRepository.findByIsActiveTrue();
    }

    @Transactional
    public SurveyResponse submitResponse(UUID surveyId, UUID studentId, String answersJson) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));
        
        if (!survey.isActive()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Khảo sát này đã đóng");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        SurveyResponse response = SurveyResponse.builder()
                .survey(survey)
                .student(student)
                .answersJson(answersJson)
                .submittedAt(LocalDateTime.now())
                .build();

        return surveyResponseRepository.save(response);
    }
}
