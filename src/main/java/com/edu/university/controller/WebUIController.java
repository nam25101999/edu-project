package com.edu.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin-ui")
public class WebUIController {

    @GetMapping("/login")
    public String loginPage() {
        return "admin-login";
    }

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        model.addAttribute("title", "Dashboard Tổng Quan");
        return "admin-dashboard";
    }

    @GetMapping("/students")
    public String studentsPage(Model model) {
        model.addAttribute("title", "Quản lý Sinh viên");
        return "admin-students";
    }
}