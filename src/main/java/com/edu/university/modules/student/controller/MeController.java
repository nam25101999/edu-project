package com.edu.university.modules.student.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edu.university.modules.schedule.dto.response.StudentScheduleResponseDTO;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final StudentService studentService;

    /**
     * Lấy thông tin hồ sơ của sinh viên đang đăng nhập.
     */
    @GetMapping("/profile")
    public ResponseEntity<BaseResponse<StudentResponseDTO>> getMyProfile() {
        return ResponseEntity.ok(BaseResponse.ok("Lấy thông tin cá nhân thành công", studentService.getCurrentStudentProfile()));
    }

    /**
     * Lấy lịch học của sinh viên đang đăng nhập.
     */
    @GetMapping("/schedule")
    public ResponseEntity<BaseResponse<PageResponse<StudentScheduleResponseDTO>>> getMySchedule(@PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage("Lấy lịch học thành công", studentService.getMySchedule(pageable)));
    }
}
