package com.edu.university.modules.enrollment.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.enrollment.dto.EnrollmentRequest;
import com.edu.university.modules.enrollment.entity.Enrollment;
import com.edu.university.modules.enrollment.service.EnrollmentService;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller xử lý đăng ký học phần.
 * Trả về kết quả qua ApiResponse chuẩn hóa.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentRepository studentRepository;

    @PostMapping("/enrollments")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<Enrollment> enrollCourse(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody EnrollmentRequest request) {
        Student student = studentRepository.findByUserId(userDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND, "Profile Student not found"));

        return ApiResponse.created("Đăng ký môn học thành công",
                enrollmentService.enroll(student.getId(), request.classSectionId()));
    }

    @PostMapping("/students/{studentId}/register-class")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ApiResponse<Enrollment> registerClassForStudent(@PathVariable UUID studentId,
                                                           @RequestBody EnrollmentRequest request) {
        return ApiResponse.created("Đăng ký lớp học phần thành công",
                enrollmentService.enroll(studentId, request.classSectionId()));
    }
}