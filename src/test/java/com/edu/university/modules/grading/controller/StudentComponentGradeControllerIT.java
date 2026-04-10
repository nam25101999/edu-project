package com.edu.university.modules.grading.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.grading.dto.request.StudentComponentGradeRequestDTO;
import com.edu.university.modules.grading.entity.GradeComponent;
import com.edu.university.modules.grading.entity.StudentComponentGrade;
import com.edu.university.modules.grading.repository.GradeComponentRepository;
import com.edu.university.modules.grading.repository.StudentComponentGradeRepository;
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
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentComponentGradeControllerIT extends BaseIntegrationTest {

    @Autowired
    private StudentComponentGradeRepository studentComponentGradeRepository;
    @Autowired
    private CourseRegistrationRepository courseRegistrationRepository;
    @Autowired
    private GradeComponentRepository gradeComponentRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    private Student testStudent;
    private CourseRegistration testRegistration;
    private GradeComponent testComponent;

    @BeforeEach
    void setUp() {
        studentComponentGradeRepository.deleteAll();
        courseRegistrationRepository.deleteAll();
        
        testStudent = studentRepository.save(Student.builder()
                .studentCode("STU_GRD")
                .fullName("Grading Student")
                .email("grd@edu.vn")
                .build());

        Course course = courseRepository.save(Course.builder()
                .courseCode("GRD202")
                .name("Comp Grade Course")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());

        Semester semester = semesterRepository.save(Semester.builder()
                .semesterName("HK1 2023-2024")
                .semesterCode("20231_GRD2")
                .build());

        CourseSection section = courseSectionRepository.save(CourseSection.builder()
                .sectionCode("GRD202_01")
                .course(course)
                .semester(semester)
                .capacity(30)
                .isActive(true)
                .build());

        testRegistration = courseRegistrationRepository.save(CourseRegistration.builder()
                .student(testStudent)
                .courseSection(section)
                .isActive(true)
                .build());

        testComponent = gradeComponentRepository.save(GradeComponent.builder()
                .componentName("Assignment")
                .weightPercentage(new BigDecimal("0.3"))
                .courseSection(section)
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void upsert_ShouldReturn200_WhenValidRequest() throws Exception {
        StudentComponentGradeRequestDTO request = new StudentComponentGradeRequestDTO();
        request.setRegistrationId(testRegistration.getId());
        request.setComponentId(testComponent.getId());
        request.setScore(new BigDecimal("8.5"));

        mockMvc.perform(post("/api/student-component-grades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(8.5));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void getByRegistrationId_ShouldReturnPage() throws Exception {
        StudentComponentGrade grade = StudentComponentGrade.builder()
                .courseRegistration(testRegistration)
                .gradeComponent(testComponent)
                .score(new BigDecimal("9.0"))
                .isActive(true)
                .build();
        studentComponentGradeRepository.save(grade);

        mockMvc.perform(get("/api/student-component-grades/registration/" + testRegistration.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }
}
