package com.edu.university.modules.studentservice.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.dto.request.SurveyResponseRequest;
import com.edu.university.modules.studentservice.entity.Survey;
import com.edu.university.modules.studentservice.repository.SurveyRepository;
import com.edu.university.modules.studentservice.repository.SurveyResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SurveyControllerIT extends BaseIntegrationTest {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Student student;
    private Survey survey;

    @BeforeEach
    void setUp() {
        surveyResponseRepository.deleteAll();
        surveyRepository.deleteAll();

        student = new Student();
        student.setStudentCode("S_SUR_001");
        student.setFullName("Survey Student");
        student = studentRepository.save(student);

        survey = Survey.builder()
                .title("Khảo sát sự hài lòng")
                .description("Mô tả khảo sát")
                .isActive(true)
                .build();
        survey = surveyRepository.save(survey);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getActiveSurveys_Success() throws Exception {
        mockMvc.perform(get("/api/surveys/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void submitResponse_Success() throws Exception {
        SurveyResponseRequest request = new SurveyResponseRequest();
        request.setStudentId(student.getId());
        request.setAnswersJson("{\"q1\":\"v1\"}");

        mockMvc.perform(post("/api/surveys/{id}/respond", survey.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.answersJson").value("{\"q1\":\"v1\"}"));
    }
}
