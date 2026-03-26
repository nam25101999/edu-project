package com.edu.university.modules.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller phục vụ giao diện người dùng (Web UI)
 * Yêu cầu: Cần có dependency spring-boot-starter-thymeleaf trong pom.xml
 */
@Controller
public class WebUIController {

    /**
     * Chuyển hướng trang chủ về trang login nếu chưa đăng nhập,
     * logic kiểm tra token sẽ do JavaScript ở client xử lý.
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    /**
     * Trang đăng nhập, đăng ký, quên mật khẩu
     */
    @GetMapping("/login")
    public String authPage() {
        // Trả về file src/main/resources/templates/auth.html
        return "auth";
    }

    /**
     * Trang hệ thống chính (Dashboard)
     */
    @GetMapping("/dashboard")
    public String dashboardPage() {
        // Trả về file src/main/resources/templates/dashboard.html
        return "dashboard";
    }
}