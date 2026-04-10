package com.edu.university.modules.student.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.hr.repository.FacultyRepository;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.repository.StudentClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class StudentClassControllerIT extends BaseIntegrationTest {

        @Autowired
        private StudentClassRepository studentClassRepository;

        @Autowired
        private DepartmentRepository departmentRepository;

        @Autowired
        private MajorRepository majorRepository;

        @Autowired
        private AcademicYearRepository academicYearRepository;

        @Autowired
        private FacultyRepository facultyRepository;

        private Department testDepartment;
        private Major testMajor;
        private AcademicYear testAcademicYear;

        @BeforeEach
        void setUp() {
                studentClassRepository.deleteAll();
                majorRepository.deleteAll();
                facultyRepository.deleteAll();
                departmentRepository.deleteAll();
                academicYearRepository.deleteAll();

                // Create Department
                testDepartment = Department.builder()
                                .code("SE")
                                .name("Software Engineering")
                                .isActive(true)
                                .build();
                testDepartment = departmentRepository.save(testDepartment);

                // Create Faculty (Major needs it)
                Faculty faculty = Faculty.builder()
                                .code("CS")
                                .name("Computer Science")
                                .isActive(true)
                                .build();
                faculty = facultyRepository.save(faculty);

                // Create Major
                testMajor = Major.builder()
                                .majorCode("CS01")
                                .name("Computer Science Major")
                                .faculty(faculty)
                                .isActive(true)
                                .build();
                testMajor = majorRepository.save(testMajor);

                // Create AcademicYear
                testAcademicYear = AcademicYear.builder()
                                .academicCode("K20")
                                .academicName("Khóa 2020")
                                .academicYear("2020-2024")
                                .isActive(true)
                                .build();
                testAcademicYear = academicYearRepository.save(testAcademicYear);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void createStudentClass_Success() throws Exception {
                StudentClassRequestDTO request = new StudentClassRequestDTO();
                request.setClassCode("D20CQCN01");
                request.setClassName("Lớp Công nghệ 1");
                request.setDepartmentId(testDepartment.getId());
                request.setMajorId(testMajor.getId());
                request.setAcademicYear("2020-2024");

                mockMvc.perform(post("/api/student-classes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.code").value(201))
                                .andExpect(jsonPath("$.data.classCode").value("D20CQCN01"))
                                .andExpect(jsonPath("$.data.departmentName").value("Software Engineering"))
                                .andExpect(jsonPath("$.data.majorName").value("Computer Science Major"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getAllClasses_Success() throws Exception {
                StudentClass sc = StudentClass.builder()
                                .classCode("D20CQCN01")
                                .className("Lớp Công nghệ 1")
                                .department(testDepartment)
                                .major(testMajor)
                                .academicYear(testAcademicYear)
                                .isActive(true)
                                .build();
                studentClassRepository.save(sc);

                mockMvc.perform(get("/api/student-classes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.content.length()").value(1))
                                .andExpect(jsonPath("$.data.content[0].classCode").value("D20CQCN01"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void getClassById_Success() throws Exception {
                StudentClass sc = StudentClass.builder()
                                .classCode("D20CQCN01")
                                .className("Lớp Công nghệ 1")
                                .department(testDepartment)
                                .major(testMajor)
                                .academicYear(testAcademicYear)
                                .isActive(true)
                                .build();
                StudentClass saved = studentClassRepository.save(sc);

                mockMvc.perform(get("/api/student-classes/{id}", saved.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.classCode").value("D20CQCN01"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateStudentClass_Success() throws Exception {
                StudentClass sc = StudentClass.builder()
                                .classCode("D20CQCN01")
                                .className("Lớp Công nghệ 1")
                                .department(testDepartment)
                                .major(testMajor)
                                .academicYear(testAcademicYear)
                                .isActive(true)
                                .build();
                StudentClass saved = studentClassRepository.save(sc);

                StudentClassRequestDTO request = new StudentClassRequestDTO();
                request.setClassCode("D20CQCN01_UPDATED");
                request.setClassName("Lớp Công nghệ 1 Updated");
                request.setDepartmentId(testDepartment.getId());
                request.setMajorId(testMajor.getId());
                request.setAcademicYear("2020-2024");

                mockMvc.perform(put("/api/student-classes/{id}", saved.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200))
                                .andExpect(jsonPath("$.data.classCode").value("D20CQCN01_UPDATED"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void deleteStudentClass_Success() throws Exception {
                StudentClass sc = StudentClass.builder()
                                .classCode("D20CQCN01")
                                .className("Lớp Công nghệ 1")
                                .department(testDepartment)
                                .major(testMajor)
                                .academicYear(testAcademicYear)
                                .isActive(true)
                                .build();
                StudentClass saved = studentClassRepository.save(sc);

                mockMvc.perform(delete("/api/student-classes/{id}", saved.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.code").value(200));

                entityManager.flush();
                entityManager.clear();

                mockMvc.perform(get("/api/student-classes"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.content.length()").value(0));
        }
}
