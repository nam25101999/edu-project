package com.edu.university.modules.elearning.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.dto.request.AttendanceRequest;
import com.edu.university.modules.elearning.entity.Attendance;
import com.edu.university.modules.elearning.repository.AttendanceRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AttendanceControllerIT extends BaseIntegrationTest {

    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private CourseSectionRepository courseSectionRepository;

    @Autowired
    private StudentRepository studentRepository;

    private CourseSection courseSection;
    private Student student;

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        
        courseSection = new CourseSection();
        courseSection.setSectionCode("CS001");
        courseSection = courseSectionRepository.save(courseSection);

        student = new Student();
        student.setStudentCode("S001");
        student.setFullName("John Doe");
        student = studentRepository.save(student);
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void createAttendance_Success() throws Exception {
        AttendanceRequest request = new AttendanceRequest();
        request.setCourseSectionId(courseSection.getId());
        request.setAttendanceDate(LocalDate.now());
        request.setNotes("Điểm danh buổi 1");
        request.setRecords(new ArrayList<>());
        
        AttendanceRequest.AttendanceRecordRequest recordRequest = new AttendanceRequest.AttendanceRecordRequest();
        recordRequest.setStudentId(student.getId());
        recordRequest.setStatus("PRESENT");
        request.getRecords().add(recordRequest);

        mockMvc.perform(post("/api/attendances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Ghi nhận điểm danh thành công"));
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void getAttendanceByCourseSection_Success() throws Exception {
        Attendance attendance = Attendance.builder()
                .courseSection(courseSection)
                .attendanceDate(LocalDate.now())
                .notes("Buổi học cũ")
                .build();
        attendanceRepository.save(attendance);

        mockMvc.perform(get("/api/attendances/course-section/{id}", courseSection.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
