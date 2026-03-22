package com.edu.university;

import com.edu.university.modules.report.service.ImportExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ImportExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImportExportService importExportService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testImportStudents_WithAdminRole_ShouldReturn200() throws Exception {
        // Tạo file Excel giả lập (MockMultipartFile)
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "students.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy excel content".getBytes()
        );

        // Giả lập Service import được 10 sinh viên
        when(importExportService.importStudentsFromExcel(any())).thenReturn(10);

        mockMvc.perform(multipart("/api/data/import/students").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Đã import thành công 10 sinh viên")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testExportStudents_WithAdminRole_ShouldReturnExcelFile() throws Exception {
        byte[] mockExcelBytes = "mock excel data".getBytes();
        when(importExportService.exportStudentList()).thenReturn(mockExcelBytes);

        mockMvc.perform(get("/api/data/export/students"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"danh_sach_sinh_vien.xlsx\""))
                .andExpect(content().bytes(mockExcelBytes));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testExportGrades_WithStudentRole_ShouldReturnExcelFile() throws Exception {
        UUID studentId = UUID.randomUUID();
        byte[] mockExcelBytes = "mock grades data".getBytes();

        when(importExportService.exportStudentGrades(studentId)).thenReturn(mockExcelBytes);

        // Sinh viên được phép xuất bảng điểm của cá nhân mình (do có hasAnyRole('ADMIN', 'STUDENT'))
        mockMvc.perform(get("/api/data/export/grades/{studentId}", studentId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"bang_diem_sinh_vien.xlsx\""))
                .andExpect(content().bytes(mockExcelBytes));
    }
}