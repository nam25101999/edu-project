package com.edu.university.modules.enrollment.controller;

import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.edu.university.modules.enrollment.dto.EnrollmentRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentRepository studentRepository;

    // API Cũ (Dùng UUID từ Token)
    @PostMapping("/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> enrollCourse(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestBody EnrollmentRequest request) {
        var student = studentRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile Student not found"));

        var result = enrollmentService.enroll(student.getId(), request.classSectionId());
        return ResponseEntity.ok(result);
    }

    // API Mới theo yêu cầu: /api/students/{studentId}/register-class
    // Có thể dùng cho Admin đăng ký dùm, hoặc Sinh viên tự đăng ký
    @PostMapping("/students/{studentId}/register-class")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<?> registerClassForStudent(@PathVariable UUID studentId,
                                                     @RequestBody EnrollmentRequest request) {
        var result = enrollmentService.enroll(studentId, request.classSectionId());
        return ResponseEntity.ok(result);
    }
}