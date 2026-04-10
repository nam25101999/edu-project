package com.edu.university.modules.studentservice.service;
 
import com.edu.university.common.exception.AppException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.dto.response.SurveyResponseDTO;
import com.edu.university.modules.studentservice.dto.response.SurveyResultResponseDTO;
import com.edu.university.modules.studentservice.entity.Survey;
import com.edu.university.modules.studentservice.entity.SurveyResponse;
import com.edu.university.modules.studentservice.mapper.SurveyMapper;
import com.edu.university.modules.studentservice.repository.SurveyRepository;
import com.edu.university.modules.studentservice.repository.SurveyResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.UUID;
 
@Service
@RequiredArgsConstructor
public class SurveyService {
 
    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository surveyResponseRepository;
    private final StudentRepository studentRepository;
    private final SurveyMapper surveyMapper;
 
    @Transactional(readOnly = true)
    public Page<SurveyResponseDTO> getActiveSurveys(Pageable pageable) {
        return surveyRepository.findByIsActiveTrue(pageable)
                .map(surveyMapper::toResponseDTO);
    }
 
    @Transactional
    public SurveyResultResponseDTO submitResponse(UUID surveyId, UUID studentId, String answersJson) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        
        if (!survey.isActive()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Khảo sát này đã đóng");
        }
 
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
 
        SurveyResponse response = SurveyResponse.builder()
                .survey(survey)
                .student(student)
                .answersJson(answersJson)
                .submittedAt(LocalDateTime.now())
                .build();
 
        return surveyMapper.toResultResponseDTO(surveyResponseRepository.save(response));
    }
}
