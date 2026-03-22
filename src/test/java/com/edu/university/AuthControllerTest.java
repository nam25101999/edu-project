package com.edu.university.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.edu.university.modules.auth.dto.AuthDtos.LoginRequest;
import com.edu.university.modules.auth.dto.AuthDtos.SignupRequest;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.auth.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Tự động xóa (rollback) dữ liệu tạo ra sau mỗi kịch bản test
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================================================
    // TỰ ĐỘNG TẠO DỮ LIỆU TEST TRƯỚC MỖI KỊCH BẢN (Thay cho data.sql)
    // =========================================================
    @BeforeEach
    public void setUp() {
        if (!userRepository.existsByUsername("admin_test")) {
            userRepository.save(User.builder()
                    .username("admin_test")
                    .password(passwordEncoder.encode("password123"))
                    .email("admin@test.com")
                    .role(Role.ROLE_ADMIN)
                    .build());
        }

        if (!userRepository.existsByUsername("student_test")) {
            userRepository.save(User.builder()
                    .username("student_test")
                    .password(passwordEncoder.encode("password123"))
                    .email("student@test.com")
                    .role(Role.ROLE_STUDENT)
                    .build());
        }

        if (!userRepository.existsByUsername("lecturer_test")) {
            userRepository.save(User.builder()
                    .username("lecturer_test")
                    .password(passwordEncoder.encode("password123"))
                    .email("lecturer@test.com")
                    .role(Role.ROLE_LECTURER)
                    .build());
        }
    }

    // =========================================================
    // 1. TEST CHỨC NĂNG LOGIN VÀ REGISTER CƠ BẢN
    // =========================================================

    @Test
    public void testAuthenticateUser_AdminLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin_test", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("admin_test"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @Test
    public void testRegisterUser_RealSave_Success() throws Exception {
        SignupRequest signupRequest = new SignupRequest("new_student", "pass123", "newstudent@test.com", "ROLE_STUDENT");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Đăng ký tài khoản thành công!"));

        assertTrue(userRepository.existsByUsername("new_student"));
    }

    // =========================================================
    // 2. TEST BẢO MẬT PHÂN QUYỀN (AUTHORIZATION)
    // =========================================================

    @Test
    public void testAccessAdminApi_WithoutToken_ShouldReturn401() throws Exception {
        // Cố tình truy cập API bảo mật mà KHÔNG gửi Token
        mockMvc.perform(get("/api/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // Mong đợi lỗi 401 Unauthorized
    }

    @Test
    public void testAccessAdminApi_WithStudentToken_ShouldReturn403() throws Exception {
        // 1. Đăng nhập bằng tài khoản Student để lấy Token
        String studentToken = getJwtTokenForUser("student_test", "password123");

        // 2. Dùng Token của Student để truy cập API của Admin (path phải đúng /api/admin/**)
        mockMvc.perform(get("/api/admin/dashboard") // ✅ đổi path admin thật
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 403 Forbidden đúng theo SecurityConfig
    }

    @Test
    public void testAccessAdminApi_WithAdminToken_ShouldReturn200() throws Exception {
        // 1. Đăng nhập bằng tài khoản Admin để lấy Token
        String adminToken = getJwtTokenForUser("admin_test", "password123");

        // 2. Dùng Token của Admin để truy cập API
        mockMvc.perform(get("/api/students")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // Mong đợi 200 OK
    }

    // =========================================================
    // HÀM HỖ TRỢ (HELPER METHOD)
    // =========================================================

    // Hàm phụ trợ giúp tự động đăng nhập và trích xuất chuỗi JWT Token
    private String getJwtTokenForUser(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        return JsonPath.read(responseString, "$.token");
    }
}