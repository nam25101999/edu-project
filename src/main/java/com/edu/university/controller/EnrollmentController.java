package com.edu.university.controller;

import com.edu.university.dto.PayloadDtos.EnrollmentRequest;
import com.edu.university.repository.StudentRepository;
import com.edu.university.security.UserDetailsImpl;
import com.edu.university.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentRepository studentRepository;

    @PostMapping
    public ResponseEntity<?> enrollCourse(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestBody EnrollmentRequest request) {
        // Lấy UUID của student từ thông tin đăng nhập
        var student = studentRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile Student not found"));

        var result = enrollmentService.enroll(student.getId(), request.classSectionId());
        return ResponseEntity.ok(result);
    }
}