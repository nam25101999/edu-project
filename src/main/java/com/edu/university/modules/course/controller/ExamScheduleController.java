package com.edu.university.modules.course.controller;

import com.edu.university.modules.course.dto.ExamScheduleDtos.ExamScheduleRequest;
import com.edu.university.common.security.UserDetailsImpl;
import com.edu.university.modules.course.service.ExamScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final ExamScheduleService examScheduleService;

    // ADMIN tạo lịch thi (Tự động check trùng phòng & trùng sinh viên)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createExamSchedule(@Valid @RequestBody ExamScheduleRequest request) {
        return ResponseEntity.ok(examScheduleService.createExamSchedule(request));
    }

    // STUDENT xem danh sách lịch thi cá nhân
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<?> getMyExamSchedules(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(examScheduleService.getMyExamSchedules(userDetails.getId()));
    }
}