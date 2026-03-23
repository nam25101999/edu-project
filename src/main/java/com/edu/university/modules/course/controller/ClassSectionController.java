package com.edu.university.modules.course.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.course.dto.ClassSectionDtos.ClassSectionRequest;
import com.edu.university.modules.course.entity.ClassSection;
import com.edu.university.modules.course.service.ClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller xử lý lớp học phần.
 * Trả về kết quả qua ApiResponse chuẩn hóa.
 */
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassSectionController {

    private final ClassSectionService classSectionService;

    @GetMapping
    public ApiResponse<Page<ClassSection>> getAllClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(classSectionService.getAllClassSections(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassSection> getClassById(@PathVariable UUID id) {
        return ApiResponse.success(classSectionService.getClassSectionById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ClassSection> createClass(@Valid @RequestBody ClassSectionRequest request) {
        return ApiResponse.created("Tạo lớp học phần thành công",
                classSectionService.createClassSection(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ClassSection> updateClass(@PathVariable UUID id, @Valid @RequestBody ClassSectionRequest request) {
        return ApiResponse.success("Cập nhật lớp học phần thành công",
                classSectionService.updateClassSection(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteClass(@PathVariable UUID id) {
        classSectionService.deleteClassSection(id);
        return ApiResponse.success("Xóa lớp học phần thành công", null);
    }
}