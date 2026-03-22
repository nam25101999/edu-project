package com.edu.university;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import com.edu.university.modules.course.dto.ClassSectionDtos.ClassSectionRequest;
import com.edu.university.modules.course.entity.ClassSection;
import com.edu.university.modules.course.service.ClassSectionService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClassSectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassSectionService classSectionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testGetAllClasses_ShouldReturn200() throws Exception {
        ClassSection mockClass = ClassSection.builder()
                .id(UUID.randomUUID())
                .semester("HK1")
                .year(2024)
                .room("A1-101")
                .schedule("T2, 1-3")
                .maxStudents(50)
                .build();
        Page<ClassSection> pageData = new PageImpl<>(List.of(mockClass));

        when(classSectionService.getAllClassSections(any(PageRequest.class))).thenReturn(pageData);

        mockMvc.perform(get("/api/classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].room").value("A1-101"))
                .andExpect(jsonPath("$.content[0].schedule").value("T2, 1-3"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateClass_WithValidData_ShouldReturn200() throws Exception {
        UUID courseId = UUID.randomUUID();
        ClassSectionRequest request = new ClassSectionRequest(
                courseId, null, "HK1", 2024, "T3, 4-6", "B2-202", 40
        );

        ClassSection mockClass = ClassSection.builder()
                .id(UUID.randomUUID())
                .semester("HK1")
                .year(2024)
                .schedule("T3, 4-6")
                .room("B2-202")
                .build();

        when(classSectionService.createClassSection(any(ClassSectionRequest.class))).thenReturn(mockClass);

        mockMvc.perform(post("/api/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room").value("B2-202"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateClass_MissingData_ShouldReturn400BadRequest() throws Exception {
        // Cố tình bỏ trống room và schedule để test Validate của DTO
        ClassSectionRequest invalidRequest = new ClassSectionRequest(
                UUID.randomUUID(), null, "HK1", 2024, "", "", null
        );

        mockMvc.perform(post("/api/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest()); // Kì vọng lỗi 400 Bad Request từ @Valid
    }
}