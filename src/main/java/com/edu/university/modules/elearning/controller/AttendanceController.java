package com.edu.university.modules.elearning.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.elearning.dto.request.AttendanceRequest;
import com.edu.university.modules.elearning.dto.response.AttendanceRecordResponseDTO;
import com.edu.university.modules.elearning.dto.response.AttendanceResponseDTO;
import com.edu.university.modules.elearning.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> createAttendance(@RequestBody AttendanceRequest request) {
        AttendanceResponseDTO attendance = attendanceService.createAttendance(request);
        return ResponseEntity.ok(ApiResponse.success("Ghi nhận điểm danh thành công", attendance));
    }

    @GetMapping("/course-section/{id}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceByCourseSection(
            @PathVariable UUID id,
            Pageable pageable) {
        List<AttendanceResponseDTO> list = attendanceService.getAttendanceByCourseSection(id, pageable).getContent();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/course-section/{id}/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceRecordResponseDTO>>> getStudentAttendanceHistory(
            @PathVariable UUID id, 
            @PathVariable UUID studentId,
            Pageable pageable) {
        List<AttendanceRecordResponseDTO> list = attendanceService.getStudentAttendanceHistory(id, studentId, pageable).getContent();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
