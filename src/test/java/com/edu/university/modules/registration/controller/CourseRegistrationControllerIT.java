package com.edu.university.modules.registration.controller;
 
import com.edu.university.BaseIntegrationTest;
import com.edu.university.builders.*;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.FacultyRepository;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.registration.repository.RegistrationPeriodRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.repository.StudentClassRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private StudentClassRepository studentClassRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private UserRepository userRepository;

    private Student student;
    private CourseSection courseSection;
    private RegistrationPeriod registrationPeriod;
    private CourseRegistration courseRegistration;

    @BeforeEach
    void setUp() {
        courseRegistrationRepository.deleteAll();
        registrationPeriodRepository.deleteAll();
        courseSectionRepository.deleteAll();
        courseRepository.deleteAll();
        semesterRepository.deleteAll();
        studentRepository.deleteAll();
        studentClassRepository.deleteAll();
        majorRepository.deleteAll();
        departmentRepository.deleteAll();
        facultyRepository.deleteAll();
        academicYearRepository.deleteAll();
        userRepository.deleteAll();

        // Use Builders for Setup
        Faculty faculty = facultyRepository.save(FacultyBuilder.aFaculty().withCode("FIT").build());
        Department dept = departmentRepository.save(DepartmentBuilder.aDepartment().withCode("SE").build());
        Major major = majorRepository.save(MajorBuilder.aMajor().withCode("SE01").withFaculty(faculty).build());
        AcademicYear ay = academicYearRepository.save(AcademicYearBuilder.anAcademicYear().withAcademicCode("K20").build());
        
        StudentClass studentClass = studentClassRepository.save(StudentClassBuilder.aStudentClass()
                .withClassCode("D20CQCN01").withDepartment(dept).withMajor(major).withAcademicYear(ay).build());
        
        Users user = userRepository.save(UsersBuilder.aUser().build());
        
        student = studentRepository.save(StudentBuilder.aStudent()
                .withStudentCode("B20DCCN001").withFullName("Nguyen Van A").withStudentClass(studentClass).withUser(user).build());

        Course course = courseRepository.save(CourseBuilder.aCourse()
                .withCode("CS101").withName("Java Programming").withDepartment(dept).build());

        Semester semester = semesterRepository.save(SemesterBuilder.aSemester()
                .withSemesterCode("HK1_2023").build());

        courseSection = courseSectionRepository.save(CourseSectionBuilder.aCourseSection()
                .withClassCode("CS101.01").withCourse(course).withSemester(semester).build());

        registrationPeriod = registrationPeriodRepository.save(RegistrationPeriodBuilder.aRegistrationPeriod()
                .withSemester(semester).build());

        courseRegistration = courseRegistrationRepository.save(CourseRegistration.builder()
                .student(student).courseSection(courseSection).registrationPeriod(registrationPeriod).registrationType(1).status(1).isActive(true).build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerCourse_Success() throws Exception {
        Course course2 = courseRepository.save(CourseBuilder.aCourse()
                .withCode("CS102").withName("Database").withDepartment(courseSection.getCourse().getDepartment()).build());
        CourseSection section2 = courseSectionRepository.save(CourseSectionBuilder.aCourseSection()
                .withClassCode("CS102.01").withCourse(course2).withSemester(courseSection.getSemester()).build());

        CourseRegistrationRequestDTO request = new CourseRegistrationRequestDTO();
        request.setStudentId(student.getId());
        request.setCourseSectionId(section2.getId());
        request.setRegistrationPeriodId(registrationPeriod.getId());
        request.setRegistrationType(1);

        mockMvc.perform(post("/api/course-registrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentCode").value("B20DCCN001"))
                .andExpect(jsonPath("$.courseCode").value("CS102"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRegistrations_Success() throws Exception {
        mockMvc.perform(get("/api/course-registrations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getRegistrationById_Success() throws Exception {
        mockMvc.perform(get("/api/course-registrations/{id}", courseRegistration.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCode").value("CS101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateRegistration_Success() throws Exception {
        CourseRegistrationRequestDTO request = new CourseRegistrationRequestDTO();
        request.setStudentId(student.getId());
        request.setCourseSectionId(courseSection.getId());
        request.setRegistrationPeriodId(registrationPeriod.getId());
        request.setRegistrationType(2); // học lại

        mockMvc.perform(put("/api/course-registrations/{id}", courseRegistration.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrationType").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRegistration_Success() throws Exception {
        mockMvc.perform(delete("/api/course-registrations/{id}", courseRegistration.getId()))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/course-registrations/{id}", courseRegistration.getId()))
                .andExpect(status().isNotFound());
    }
}
