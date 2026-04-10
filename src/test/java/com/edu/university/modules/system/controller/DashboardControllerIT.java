package com.edu.university.modules.system.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.builders.FacultyBuilder;
import com.edu.university.builders.MajorBuilder;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.hr.repository.FacultyRepository;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import com.edu.university.modules.registration.repository.EquivalentCourseRepository;
import com.edu.university.modules.graduation.repository.GraduationConditionRepository;
import com.edu.university.modules.grading.repository.GradeScaleRepository;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

public class DashboardControllerIT extends BaseIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private TrainingProgramRepository trainingProgramRepository;

    @Autowired
    private TuitionFeeRepository tuitionFeeRepository;

    @Autowired
    private EquivalentCourseRepository equivalentCourseRepository;

    @Autowired
    private GraduationConditionRepository graduationConditionRepository;

    @Autowired
    private GradeScaleRepository gradeScaleRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    void setUp() {
        // SIMPLE CLEANUP: Only tables related to dashboard stats
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        
        String[] tables = {
            "equivalent_courses", "course_registrations", "student_tuitions", "tuition_fees",
            "graduation_results", "graduation_conditions", "student_component_grades", "grade_scales",
            "exam_results", "exam_registrations", "exams", "attendance_records", "submissions",
            "materials", "assignments", "student_course_sections", "course_sections",
            "training_program_courses", "training_programs", "course_prerequisites", "courses",
            "student_classes", "student_class_section", "advisor_class_section", "lecturer_course_class",
            "students", "student_status", "academic_years", "semesters", "registration_periods",
            "employee_positions", "employees", "departments", "faculties", "majors",
            "login_sessions", "refresh_tokens", "otp_tokens", "user_roles", "users", "roles"
        };
        
        for (String table : tables) {
            try {
                entityManager.createNativeQuery("DELETE FROM " + table).executeUpdate();
            } catch (Exception e) {
                // Ignore if table doesn't exist yet or other issues
            }
        }
        
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        
        // Setup REQUIRED role for getStats() using builder
        roleRepository.save(Role.builder()
                .name("ROLE_LECTURER")
                .description("Lecturer")
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStats_ShouldReturn200_WhenAdmin() throws Exception {
        // Setup some data
        Faculty faculty = facultyRepository.save(FacultyBuilder.aFaculty().build());
        majorRepository.save(MajorBuilder.aMajor()
                .withName("Computer Science")
                .withCode("CS01")
                .withFaculty(faculty)
                .build());
        courseRepository.save(Course.builder().name("Java Programming").courseCode("JAVA01").credits(new BigDecimal("3")).isActive(true).build());
        
        majorRepository.flush();
        courseRepository.flush();

        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalCourses").exists())
                .andExpect(jsonPath("$.data.totalMajors").exists());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getStats_ShouldReturn403_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStats_ShouldReturn401_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard/stats"))
                .andExpect(status().isUnauthorized());
    }
}
