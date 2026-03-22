package com.edu.university;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.edu.university.modules.course.dto.CourseDtos.CourseRequest;
import com.edu.university.modules.course.entity.Course;
import com.edu.university.modules.course.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testGetAllCourses_ShouldReturn200() throws Exception {
        Course mockCourse = Course.builder()
                .id(UUID.randomUUID())
                .courseCode("IT001")
                .name("Nhập môn lập trình")
                .credits(3)
                .build();
        Page<Course> coursePage = new PageImpl<>(List.of(mockCourse));

        when(courseService.getAllCourses(any(PageRequest.class))).thenReturn(coursePage);

        mockMvc.perform(get("/api/courses")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].courseCode").value("IT001"))
                .andExpect(jsonPath("$.content[0].name").value("Nhập môn lập trình"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateCourse_WithAdminRole_ShouldReturn200() throws Exception {
        CourseRequest request = new CourseRequest("IT002", "Cấu trúc dữ liệu", 4, null);
        Course mockCourse = Course.builder()
                .id(UUID.randomUUID())
                .courseCode("IT002")
                .name("Cấu trúc dữ liệu")
                .credits(4)
                .build();

        when(courseService.createCourse(any(CourseRequest.class))).thenReturn(mockCourse);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCode").value("IT002"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testCreateCourse_WithStudentRole_ShouldReturn403() throws Exception {
        CourseRequest request = new CourseRequest("IT002", "Cấu trúc dữ liệu", 4, null);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden()); // Bị chặn do chỉ ADMIN mới được tạo
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteCourse_ShouldReturn200() throws Exception {
        UUID courseId = UUID.randomUUID();
        doNothing().when(courseService).deleteCourse(courseId);

        mockMvc.perform(delete("/api/courses/{id}", courseId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Xóa môn học thành công"));
    }
}