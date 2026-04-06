package com.edu.university.modules.academic.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.dto.request.LecturerCourseClassRequestDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.LecturerCourseClass;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.LecturerCourseClassRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LecturerCourseClassControllerIT extends BaseIntegrationTest {

    @Autowired
    private LecturerCourseClassRepository lecturerCourseClassRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Users testLecturer;
    private CourseSection testCourseSection;

    @BeforeEach
    void setUp() {
        lecturerCourseClassRepository.deleteAll();
        courseSectionRepository.deleteAll();
        userRepository.deleteAll();
        courseRepository.deleteAll();
        semesterRepository.deleteAll();
        roleRepository.deleteAll();

        Role lecturerRole = Role.builder().name("ROLE_LECTURER").build();
        roleRepository.save(lecturerRole);

        testLecturer = Users.builder()
                .username("lecturer_test")
                .password("password")
                .email("lecturer@example.com")
                .roles(new HashSet<>())
                .isActive(true)
                .build();
        testLecturer.getRoles().add(lecturerRole);
        testLecturer = userRepository.save(testLecturer);

        Course testCourse = Course.builder()
                .code("CS101")
                .name("Computer Science 101")
                .credits(new BigDecimal("3.0"))
                .courseType("REQUIRED")
                .isActive(true)
                .build();
        testCourse = courseRepository.save(testCourse);

        Semester testSemester = Semester.builder()
                .semesterCode("HK1_2023")
                .semesterName("Học kỳ 1 năm 2023-2024")
                .academicYear("2023-2024")
                .startDate(LocalDate.of(2023, 9, 1))
                .endDate(LocalDate.of(2024, 1, 31))
                .isActive(true)
                .build();
        testSemester = semesterRepository.save(testSemester);

        testCourseSection = CourseSection.builder()
                .classCode("L01_CS101")
                .course(testCourse)
                .semester(testSemester)
                .academicYear("2023-2024")
                .isActive(true)
                .build();
        testCourseSection = courseSectionRepository.save(testCourseSection);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignLecturerToClass_Success() throws Exception {
        LecturerCourseClassRequestDTO request = new LecturerCourseClassRequestDTO();
        request.setLecturerId(testLecturer.getId());
        request.setCourseSectionId(testCourseSection.getId());
        request.setRole("MAIN_LECTURER");

        mockMvc.perform(post("/api/lecturer-course-classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.lecturerUsername").value("lecturer_test"))
                .andExpect(jsonPath("$.data.classCode").value("L01_CS101"))
                .andExpect(jsonPath("$.data.role").value("MAIN_LECTURER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllLecturerAssignments_Success() throws Exception {
        LecturerCourseClass assignment = LecturerCourseClass.builder()
                .lecturer(testLecturer)
                .courseSection(testCourseSection)
                .role("MAIN_LECTURER")
                .isActive(true)
                .build();
        lecturerCourseClassRepository.save(assignment);

        mockMvc.perform(get("/api/lecturer-course-classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLecturerAssignmentById_Success() throws Exception {
        LecturerCourseClass assignment = LecturerCourseClass.builder()
                .lecturer(testLecturer)
                .courseSection(testCourseSection)
                .role("MAIN_LECTURER")
                .isActive(true)
                .build();
        LecturerCourseClass saved = lecturerCourseClassRepository.save(assignment);

        mockMvc.perform(get("/api/lecturer-course-classes/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.lecturerUsername").value("lecturer_test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteLecturerAssignment_Success() throws Exception {
        LecturerCourseClass assignment = LecturerCourseClass.builder()
                .lecturer(testLecturer)
                .courseSection(testCourseSection)
                .role("MAIN_LECTURER")
                .isActive(true)
                .build();
        LecturerCourseClass saved = lecturerCourseClassRepository.save(assignment);

        mockMvc.perform(delete("/api/lecturer-course-classes/{id}", saved.getId()))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/lecturer-course-classes/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
