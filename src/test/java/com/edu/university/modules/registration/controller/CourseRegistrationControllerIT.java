package com.edu.university.modules.registration.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.dto.request.EligibilityCheckRequest;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.registration.repository.RegistrationPeriodRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseRegistrationControllerIT extends BaseIntegrationTest {

    @Autowired
    private CourseRegistrationRepository courseRegistrationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseSectionRepository courseSectionRepository;
    @Autowired
    private RegistrationPeriodRepository registrationPeriodRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SemesterRepository semesterRepository;

    private Student testStudent;
    private CourseSection testSection;
    private RegistrationPeriod testPeriod;

    @BeforeEach
    void setUp() {
        courseRegistrationRepository.deleteAll();
        
        testStudent = studentRepository.save(Student.builder()
                .studentCode("STU_REG")
                .fullName("Registration Student")
                .email("reg@edu.vn")
                .build());

        Course course = courseRepository.save(Course.builder()
                .courseCode("CS101")
                .name("Intro to CS")
                .credits(new BigDecimal("3"))
                .isActive(true)
                .build());

        Semester semester = semesterRepository.save(Semester.builder()
                .semesterName("HK1 2023-2024")
                .semesterCode("20231_REG")
                .build());

        testSection = courseSectionRepository.save(CourseSection.builder()
                .sectionCode("CS101_01")
                .course(course)
                .semester(semester)
                .capacity(30)
                .isActive(true)
                .build());

        testPeriod = registrationPeriodRepository.save(RegistrationPeriod.builder()
                .name("Dot 1 HK1")
                .semester(semester)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(5))
                .minCredits(3)
                .maxCredits(25)
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        CourseRegistrationRequestDTO request = new CourseRegistrationRequestDTO();
        request.setStudentId(testStudent.getId());
        request.setCourseSectionId(testSection.getId());
        request.setRegistrationPeriodId(testPeriod.getId());

        mockMvc.perform(post("/api/course-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.studentId").value(testStudent.getId().toString()))
                .andExpect(jsonPath("$.data.courseSectionId").value(testSection.getId().toString()));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAll_ShouldReturnPage() throws Exception {
        CourseRegistration reg = CourseRegistration.builder()
                .student(testStudent)
                .courseSection(testSection)
                .registrationPeriod(testPeriod)
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build();
        courseRegistrationRepository.save(reg);

        mockMvc.perform(get("/api/course-registrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void checkEligibility_ShouldReturnTrue_WhenNoViolations() throws Exception {
        EligibilityCheckRequest request = new EligibilityCheckRequest();
        request.setStudentId(testStudent.getId());
        request.setRegistrationPeriodId(testPeriod.getId());
        request.setCourseSectionIds(Collections.singletonList(testSection.getId()));

        mockMvc.perform(post("/api/course-registrations/check-eligibility")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.eligible").value(true));
    }
}
