package com.edu.university.modules.grading.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.grading.dto.request.StudentSummaryRequestDTO;
import com.edu.university.modules.grading.entity.GradeScale;
import com.edu.university.modules.grading.entity.StudentSummary;
import com.edu.university.modules.grading.repository.GradeScaleRepository;
import com.edu.university.modules.grading.repository.StudentSummaryRepository;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentSummaryControllerIT extends BaseIntegrationTest {

    @Autowired
    private StudentSummaryRepository studentSummaryRepository;
    @Autowired
    private CourseRegistrationRepository courseRegistrationRepository;
    @Autowired
    private GradeScaleRepository gradeScaleRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    private CourseRegistration testRegistration;
    private GradeScale testScale;

    @BeforeEach
    void setUp() {
        Student student = studentRepository.save(Student.builder()
                .studentCode("STU_SUMMARY")
                .fullName("Sinh vien tong ket")
                .email("student.summary@edu.vn")
                .isActive(true)
                .build());

        Course course = courseRepository.save(Course.builder()
                .courseCode("GRD_SUM_101")
                .name("Mon tong ket")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());

        Semester semester = semesterRepository.save(Semester.builder()
                .semesterName("Hoc ky tong ket")
                .semesterCode("SEM_SUMMARY")
                .build());

        CourseSection section = courseSectionRepository.save(CourseSection.builder()
                .sectionCode("GRD_SUM_101_01")
                .course(course)
                .semester(semester)
                .capacity(40)
                .isActive(true)
                .build());

        testRegistration = courseRegistrationRepository.save(CourseRegistration.builder()
                .student(student)
                .courseSection(section)
                .registrationType(1)
                .status(1)
                .isPaid(true)
                .isActive(true)
                .build());

        testScale = gradeScaleRepository.save(GradeScale.builder()
                .scaleCode("A")
                .minScore(new BigDecimal("8.50"))
                .maxScore(new BigDecimal("10.00"))
                .letterGrade("A")
                .gpaValue(new BigDecimal("4.00"))
                .pass(true)
                .active(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void upsert_ShouldReturnSummary_WhenValidRequest() throws Exception {
        StudentSummaryRequestDTO request = new StudentSummaryRequestDTO();
        request.setRegistrationId(testRegistration.getId());
        request.setTotalScore(new BigDecimal("8.80"));
        request.setScaleId(testScale.getId());
        request.setLetterGrade("A");
        request.setGpaValue(new BigDecimal("4.00"));
        request.setResult("PASS");
        request.setFinalized(true);

        mockMvc.perform(post("/api/student-summaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.registrationId").value(testRegistration.getId().toString()))
                .andExpect(jsonPath("$.data.totalScore").value(8.8))
                .andExpect(jsonPath("$.data.letterGrade").value("A"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void getByRegistrationId_ShouldReturnStoredSummary() throws Exception {
        studentSummaryRepository.save(StudentSummary.builder()
                .courseRegistration(testRegistration)
                .gradeScale(testScale)
                .totalScore(new BigDecimal("7.50"))
                .letterGrade("B")
                .gpaValue(new BigDecimal("3.20"))
                .result("PASS")
                .isFinalized(false)
                .isActive(true)
                .build());

        mockMvc.perform(get("/api/student-summaries/registration/{registrationId}", testRegistration.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.registrationId").value(testRegistration.getId().toString()))
                .andExpect(jsonPath("$.data.studentCode").value("STU_SUMMARY"))
                .andExpect(jsonPath("$.data.courseName").value("Mon tong ket"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void delete_ShouldSoftDeleteSummary() throws Exception {
        StudentSummary saved = studentSummaryRepository.save(StudentSummary.builder()
                .courseRegistration(testRegistration)
                .gradeScale(testScale)
                .totalScore(new BigDecimal("6.50"))
                .letterGrade("C")
                .gpaValue(new BigDecimal("2.50"))
                .result("PASS")
                .isFinalized(true)
                .isActive(true)
                .build());

        mockMvc.perform(delete("/api/student-summaries/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true));

        entityManager.flush();
        entityManager.clear();

        assertFalse(studentSummaryRepository.findById(saved.getId()).isPresent());
    }
}
