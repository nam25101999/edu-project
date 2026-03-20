package com.edu.university.controller;

import com.edu.university.dto.ExamScheduleDtos.ExamScheduleRequest;
import com.edu.university.security.UserDetailsImpl;
import com.edu.university.service.ExamScheduleService;
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