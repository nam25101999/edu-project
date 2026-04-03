package com.edu.university.modules.student.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.university.modules.schedule.dto.response.StudentScheduleResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final StudentService studentService;

    /**
     * Lấy thông tin hồ sơ của sinh viên đang đăng nhập.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getMyProfile() {
        StudentResponseDTO profile = studentService.getCurrentStudentProfile();
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin cá nhân thành công", profile));
    }

    /**
     * Lấy lịch học của sinh viên đang đăng nhập.
     */
    @GetMapping("/schedule")
    public ResponseEntity<ApiResponse<List<StudentScheduleResponseDTO>>> getMySchedule() {
        List<StudentScheduleResponseDTO> schedule = studentService.getMySchedule();
        return ResponseEntity.ok(ApiResponse.success("Lấy lịch học thành công", schedule));
    }
}
