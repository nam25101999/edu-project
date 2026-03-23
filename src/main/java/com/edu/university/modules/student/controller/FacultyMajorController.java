package com.edu.university.modules.student.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.student.dto.FacultyMajorDtos.*;
import com.edu.university.modules.student.entity.Faculty;
import com.edu.university.modules.student.entity.Major;
import com.edu.university.modules.student.service.FacultyMajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller quản lý cấu trúc tổ chức (Khoa/Ngành).
 * Trả về kết quả qua ApiResponse chuẩn hóa.
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class FacultyMajorController {

    private final FacultyMajorService service;

    // === API KHOA (FACULTY) ===
    @GetMapping("/faculties")
    public ApiResponse<List<Faculty>> getAllFaculties() {
        return ApiResponse.success(service.getAllFaculties());
    }

    @PostMapping("/faculties")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Faculty> createFaculty(@Valid @RequestBody FacultyRequest request) {
        return ApiResponse.created("Tạo mới khoa thành công", service.createFaculty(request));
    }

    // === API NGÀNH (MAJOR) ===
    @GetMapping("/majors")
    public ApiResponse<List<Major>> getAllMajors() {
        return ApiResponse.success(service.getAllMajors());
    }

    @GetMapping("/faculties/{facultyId}/majors")
    public ApiResponse<List<Major>> getMajorsByFaculty(@PathVariable UUID facultyId) {
        return ApiResponse.success(service.getMajorsByFaculty(facultyId));
    }

    @PostMapping("/majors")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Major> createMajor(@Valid @RequestBody MajorRequest request) {
        return ApiResponse.created("Tạo mới ngành học thành công", service.createMajor(request));
    }
}