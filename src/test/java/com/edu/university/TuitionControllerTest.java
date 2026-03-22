package com.edu.university;

import com.edu.university.modules.finance.entity.PaymentHistory;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.entity.TuitionStatus;
import com.edu.university.modules.finance.service.TuitionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class TuitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TuitionService tuitionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testCalculateTuition_ShouldReturn200() throws Exception {
        UUID studentId = UUID.randomUUID();
        TuitionFee mockFee = TuitionFee.builder()
                .id(UUID.randomUUID())
                .totalAmount(15000000.0)
                .status(TuitionStatus.CHUA_DONG)
                .build();

        when(tuitionService.calculateTuition(eq(studentId), eq("HK1"), eq(2024))).thenReturn(mockFee);

        mockMvc.perform(post("/api/tuition/calculate")
                        .param("studentId", studentId.toString())
                        .param("semester", "HK1")
                        .param("year", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(15000000.0))
                .andExpect(jsonPath("$.status").value("CHUA_DONG"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testMakePayment_WithAdminRole_ShouldReturn200() throws Exception {
        UUID tuitionFeeId = UUID.randomUUID();
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", 5000000.0);
        paymentData.put("method", "CHUYEN_KHOAN");
        paymentData.put("note", "Đóng học phí đợt 1");

        PaymentHistory mockPayment = PaymentHistory.builder()
                .id(UUID.randomUUID())
                .amountPaid(5000000.0)
                .paymentMethod("CHUYEN_KHOAN")
                .build();

        when(tuitionService.makePayment(eq(tuitionFeeId), anyDouble(), anyString(), anyString())).thenReturn(mockPayment);

        mockMvc.perform(post("/api/tuition/{tuitionFeeId}/pay", tuitionFeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountPaid").value(5000000.0))
                .andExpect(jsonPath("$.paymentMethod").value("CHUYEN_KHOAN"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testMakePayment_WithStudentRole_ShouldReturn403() throws Exception {
        UUID tuitionFeeId = UUID.randomUUID();
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("amount", 5000000.0);

        // Sinh viên KHÔNG được phép gọi API đóng học phí (Theo cấu hình bảo mật @PreAuthorize("hasRole('ADMIN')"))
        mockMvc.perform(post("/api/tuition/{tuitionFeeId}/pay", tuitionFeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentData)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    public void testExportInvoice_ShouldReturnText() throws Exception {
        UUID tuitionFeeId = UUID.randomUUID();

        when(tuitionService.getPaymentHistory(tuitionFeeId)).thenReturn(List.of());

        mockMvc.perform(get("/api/tuition/{tuitionFeeId}/invoice/export", tuitionFeeId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("===== HÓA ĐƠN HỌC PHÍ =====")));
    }
}