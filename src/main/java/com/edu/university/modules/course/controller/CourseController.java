package com.edu.university.modules.course.controller;

import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.course.dto.CourseDtos.CourseRequest;
import com.edu.university.modules.course.entity.Course;
import com.edu.university.modules.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller xử lý danh mục môn học.
 * Trả về kết quả qua ApiResponse chuẩn hóa.
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ApiResponse<Page<Course>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(courseService.getAllCourses(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<Course> getCourseById(@PathVariable UUID id) {
        return ApiResponse.success(courseService.getCourseById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Course> createCourse(@Valid @RequestBody CourseRequest request) {
        return ApiResponse.created("Tạo môn học thành công", courseService.createCourse(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Course> updateCourse(@PathVariable UUID id, @Valid @RequestBody CourseRequest request) {
        return ApiResponse.success("Cập nhật môn học thành công", courseService.updateCourse(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ApiResponse.success("Xóa môn học thành công", null);
    }
}