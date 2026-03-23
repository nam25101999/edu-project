package com.edu.university.modules.course.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.course.dto.ExamScheduleDtos.ExamScheduleRequest;
import com.edu.university.modules.course.entity.ExamSchedule;
import com.edu.university.modules.course.service.ExamScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller xử lý lịch thi.
 * Trả về kết quả qua ApiResponse chuẩn hóa.
 */
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final ExamScheduleService examScheduleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ExamSchedule> createExamSchedule(@Valid @RequestBody ExamScheduleRequest request) {
        return ApiResponse.created("Tạo lịch thi thành công",
                examScheduleService.createExamSchedule(request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ApiResponse<List<ExamSchedule>> getMyExamSchedules(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ApiResponse.success(examScheduleService.getMyExamSchedules(userDetails.getId()));
    }
}