package com.edu.university.modules.student.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.FacultyRepository;
import com.edu.university.modules.student.dto.request.StudentClassSectionRequestDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.entity.StudentClassSection;
import com.edu.university.modules.student.repository.StudentClassRepository;
import com.edu.university.modules.student.repository.StudentClassSectionRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudentClassSectionControllerIT extends BaseIntegrationTest {

    @Autowired
    private StudentClassSectionRepository studentClassSectionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentClassRepository studentClassRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    private Student testStudent;
    private StudentClass testStudentClass;

    @BeforeEach
    void setUp() {
        studentClassSectionRepository.deleteAll();
        studentRepository.deleteAll();
        studentClassRepository.deleteAll();
        majorRepository.deleteAll();
        facultyRepository.deleteAll();
        departmentRepository.deleteAll();
        academicYearRepository.deleteAll();

        // Create Department
        Department dept = Department.builder()
                .code("SE")
                .name("Software Engineering")
                .isActive(true)
                .build();
        dept = departmentRepository.save(dept);

        // Create Faculty
        Faculty faculty = Faculty.builder()
                .code("CS")
                .name("Computer Science")
                .isActive(true)
                .build();
        faculty = facultyRepository.save(faculty);

        // Create Major
        Major major = Major.builder()
                .code("CS01")
                .name("Computer Science Major")
                .faculty(faculty)
                .isActive(true)
                .build();
        major = majorRepository.save(major);

        // Create AcademicYear
        AcademicYear ay = AcademicYear.builder()
                .academicCode("K20")
                .academicYear("2020-2024")
                .isActive(true)
                .build();
        ay = academicYearRepository.save(ay);

        // Create StudentClass
        testStudentClass = StudentClass.builder()
                .classCode("D20CQCN01")
                .className("Lớp Công nghệ 1")
                .department(dept)
                .major(major)
                .academicYear(ay)
                .isActive(true)
                .build();
        testStudentClass = studentClassRepository.save(testStudentClass);

        // Create Student
        testStudent = Student.builder()
                .studentCode("N20DCCN001")
                .fullName("Nguyen Van A")
                .studentClass(testStudentClass)
                .isActive(true)
                .build();
        testStudent = studentRepository.save(testStudent);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addStudentToClass_Success() throws Exception {
        StudentClassSectionRequestDTO request = new StudentClassSectionRequestDTO();
        request.setStudentId(testStudent.getId());
        request.setStudentClassesId(testStudentClass.getId());
        request.setStatus("Đang học");
        request.setStartDate(LocalDate.now());

        mockMvc.perform(post("/api/student-class-sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.studentCode").value("N20DCCN001"))
                .andExpect(jsonPath("$.data.className").value("Lớp Công nghệ 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAll_Success() throws Exception {
        StudentClassSection scs = StudentClassSection.builder()
                .student(testStudent)
                .studentClass(testStudentClass)
                .status("Đang học")
                .startDate(LocalDateTime.now())
                .isActive(true)
                .build();
        studentClassSectionRepository.save(scs);

        mockMvc.perform(get("/api/student-class-sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByStudentId_Success() throws Exception {
        StudentClassSection scs = StudentClassSection.builder()
                .student(testStudent)
                .studentClass(testStudentClass)
                .status("Đang học")
                .startDate(LocalDateTime.now())
                .isActive(true)
                .build();
        studentClassSectionRepository.save(scs);

        mockMvc.perform(get("/api/student-class-sections/student/{studentId}", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_Success() throws Exception {
        StudentClassSection scs = StudentClassSection.builder()
                .student(testStudent)
                .studentClass(testStudentClass)
                .status("Đang học")
                .startDate(LocalDateTime.now())
                .isActive(true)
                .build();
        StudentClassSection saved = studentClassSectionRepository.save(scs);

        StudentClassSectionRequestDTO request = new StudentClassSectionRequestDTO();
        request.setStudentId(testStudent.getId());
        request.setStudentClassesId(testStudentClass.getId());
        request.setStatus("Đã chuyển lớp");
        request.setStartDate(LocalDate.now());

        mockMvc.perform(put("/api/student-class-sections/{id}", saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("Đã chuyển lớp"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_Success() throws Exception {
        StudentClassSection scs = StudentClassSection.builder()
                .student(testStudent)
                .studentClass(testStudentClass)
                .status("Đang học")
                .startDate(LocalDateTime.now())
                .isActive(true)
                .build();
        StudentClassSection saved = studentClassSectionRepository.save(scs);

        mockMvc.perform(delete("/api/student-class-sections/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/student-class-sections/student/{studentId}", testStudent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }
}
