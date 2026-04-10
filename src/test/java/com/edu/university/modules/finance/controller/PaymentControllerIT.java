package com.edu.university.modules.finance.controller;

import com.edu.university.BaseIntegrationTest;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.finance.dto.request.PaymentRequestDTO;
import com.edu.university.modules.finance.entity.Payment;
import com.edu.university.modules.finance.entity.StudentTuition;
import com.edu.university.modules.finance.repository.PaymentRepository;
import com.edu.university.modules.finance.repository.StudentTuitionRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentControllerIT extends BaseIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentTuitionRepository studentTuitionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    private StudentTuition testTuition;

    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
        studentTuitionRepository.deleteAll();

        Student student = studentRepository.save(Student.builder()
                .studentCode("STU001")
                .fullName("Test Student")
                .email("test@edu.vn")
                .build());

        Semester semester = semesterRepository.save(Semester.builder()
                .semesterName("HK1 2023-2024")
                .semesterCode("20231")
                .build());

        testTuition = studentTuitionRepository.save(StudentTuition.builder()
                .student(student)
                .semester(semester)
                .netAmount(new BigDecimal("15000000"))
                .paidAmount(BigDecimal.ZERO)
                .status(3) // 3-DEBT
                .isActive(true)
                .build());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        PaymentRequestDTO request = new PaymentRequestDTO();
        request.setStudentTuitionId(testTuition.getId());
        request.setAmountPaid(new BigDecimal("5000000"));
        request.setPaymentMethod(2); // 2-Cash
        request.setTransactionRef("TXN123");

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amountPaid").value(5000000.0))
                .andExpect(jsonPath("$.data.paymentMethod").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByStudentTuitionId_ShouldReturnPageOfPayments() throws Exception {
        Payment payment = Payment.builder()
                .studentTuition(testTuition)
                .amountPaid(new BigDecimal("2000000"))
                .paymentMethod(1) // 1-Bank Transfer
                .isActive(true)
                .build();
        paymentRepository.save(payment);

        mockMvc.perform(get("/api/payments/tuition/" + testTuition.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].amountPaid").value(2000000.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldReturn200_WhenExists() throws Exception {
        Payment payment = Payment.builder()
                .studentTuition(testTuition)
                .amountPaid(new BigDecimal("1000000"))
                .isActive(true)
                .build();
        Payment saved = paymentRepository.save(payment);

        mockMvc.perform(delete("/api/payments/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa thanh toán thành công"));
        
        Payment deleted = paymentRepository.findById(saved.getId()).orElse(null);
        org.junit.jupiter.api.Assertions.assertNotNull(deleted);
        org.junit.jupiter.api.Assertions.assertFalse(deleted.isActive());
    }
}
