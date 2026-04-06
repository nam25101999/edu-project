package com.edu.university.modules.elearning.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.elearning.dto.request.GradeRequest;
import com.edu.university.modules.elearning.dto.request.SubmissionRequest;
import com.edu.university.modules.elearning.entity.Assignment;
import com.edu.university.modules.elearning.entity.Submission;
import com.edu.university.modules.elearning.repository.AssignmentRepository;
import com.edu.university.modules.elearning.repository.SubmissionRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SubmissionControllerIT extends BaseIntegrationTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Assignment assignment;
    private Student student;
    private Submission submission;

    @BeforeEach
    void setUp() {
        submissionRepository.deleteAll();
        assignmentRepository.deleteAll();

        assignment = new Assignment();
        assignment.setCourseSectionId(UUID.randomUUID());
        assignment.setTitle("Bài tập lớn");
        assignment.setDueDate(LocalDateTime.now().plusDays(2));
        assignment = assignmentRepository.save(assignment);

        student = new Student();
        student.setStudentCode("S002");
        student.setFullName("Jane Smith");
        student = studentRepository.save(student);

        submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .content("Nội dung bài nộp")
                .submittedAt(LocalDateTime.now())
                .build();
        submission = submissionRepository.save(submission);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createSubmission_Success() throws Exception {
        SubmissionRequest request = new SubmissionRequest();
        request.setAssignmentId(assignment.getId());
        request.setStudentId(student.getId());
        request.setContent("Bài nộp mới");
        request.setFileUrl("http://example.com/file.pdf");

        mockMvc.perform(post("/api/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Bài nộp mới"));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void gradeSubmission_Success() throws Exception {
        GradeRequest request = new GradeRequest();
        request.setScore(9.5);
        request.setFeedback("Rất tốt");

        mockMvc.perform(put("/api/submissions/{id}/grade", submission.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.score").value(9.5));
    }
}
