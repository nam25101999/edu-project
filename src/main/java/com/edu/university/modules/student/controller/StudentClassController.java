package com.edu.university.modules.student.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.student.dto.StudentClassDtos.StudentClassRequest;
import com.edu.university.modules.student.dto.StudentClassDtos.StudentClassResponse;
import com.edu.university.modules.student.service.StudentClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-classes")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassService studentClassService;

    // ✅ FIX: dùng DTO
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<StudentClassResponse>> getAllClasses() {
        return ApiResponse.success(studentClassService.getAllClasses());
    }

    // ✅ FIX: Page<StudentClassResponse>
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ApiResponse<Page<StudentClassResponse>> searchStudentClasses(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.success(
                studentClassService.searchStudentClasses(keyword, page, size)
        );
    }

    // ✅ FIX: Response DTO
    @GetMapping("/{id}")
    public ApiResponse<StudentClassResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(studentClassService.getById(id));
    }

    // ✅ FIX
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StudentClassResponse> createStudentClass(
            @Valid @RequestBody StudentClassRequest request
    ) {
        return ApiResponse.created(
                "Tạo lớp sinh hoạt thành công",
                studentClassService.createStudentClass(request)
        );
    }

    // ✅ FIX
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StudentClassResponse> updateStudentClass(
            @PathVariable UUID id,
            @Valid @RequestBody StudentClassRequest request
    ) {
        return ApiResponse.success(
                "Cập nhật lớp sinh hoạt thành công",
                studentClassService.updateStudentClass(id, request)
        );
    }

    // DELETE giữ nguyên
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteStudentClass(@PathVariable UUID id) {
        studentClassService.deleteStudentClass(id);
        return ApiResponse.success("Xóa lớp sinh hoạt thành công", null);
    }
}