package com.edu.university.modules.elearning.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.entity.Material;
import com.edu.university.modules.elearning.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MaterialControllerIT extends BaseIntegrationTest {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    private CourseSection courseSection;

    @BeforeEach
    void setUp() {
        materialRepository.deleteAll();
        courseSection = new CourseSection();
        courseSection.setSectionCode("CS_MAT_001");
        courseSection = courseSectionRepository.save(courseSection);
    }

    @Test
    @WithMockUser(roles = "LECTURER")
    void createMaterial_WithFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test content".getBytes()
        );

        mockMvc.perform(multipart("/api/materials")
                .file(file)
                .param("courseSectionId", courseSection.getId().toString())
                .param("title", "Tài liệu học tập")
                .param("description", "Mô tả tài liệu")
                .param("fileType", "PDF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Tài liệu học tập"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getMaterialsByCourseSection_Success() throws Exception {
        Material material = Material.builder()
                .courseSection(courseSection)
                .title("Tài liệu cũ")
                .build();
        materialRepository.save(material);

        mockMvc.perform(get("/api/materials/course-section/{id}", courseSection.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
