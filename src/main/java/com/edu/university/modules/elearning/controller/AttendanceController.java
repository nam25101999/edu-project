package com.edu.university.modules.elearning.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.elearning.dto.request.AttendanceRequest;
import com.edu.university.modules.elearning.entity.Attendance;
import com.edu.university.modules.elearning.entity.AttendanceRecord;
import com.edu.university.modules.elearning.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<BaseResponse<Attendance>> createAttendance(@RequestBody AttendanceRequest request) {
        Attendance attendance = attendanceService.createAttendance(request);
        return ResponseEntity.ok(BaseResponse.ok("Ghi nhận điểm danh thành công", attendance));
    }

    @GetMapping("/course-section/{id}")
    public ResponseEntity<BaseResponse<List<Attendance>>> getAttendanceByCourseSection(@PathVariable UUID id) {
        List<Attendance> attendances = attendanceService.getAttendanceByCourseSection(id);
        return ResponseEntity.ok(BaseResponse.ok(attendances));
    }

    @GetMapping("/course-section/{id}/student/{studentId}")
    public ResponseEntity<BaseResponse<List<AttendanceRecord>>> getStudentAttendanceHistory(@PathVariable UUID id, @PathVariable UUID studentId) {
        List<AttendanceRecord> history = attendanceService.getStudentAttendanceHistory(id, studentId);
        return ResponseEntity.ok(BaseResponse.ok(history));
    }
}
