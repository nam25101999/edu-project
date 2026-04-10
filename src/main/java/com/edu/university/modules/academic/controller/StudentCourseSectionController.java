package com.edu.university.modules.academic.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.academic.dto.request.StudentCourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.StudentCourseSectionResponseDTO;
import com.edu.university.modules.academic.service.StudentCourseSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/student-course-sections")
@RequiredArgsConstructor
@Validated
public class StudentCourseSectionController {

    private final StudentCourseSectionService studentCourseSectionService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentCourseSectionResponseDTO>> create(@Valid @RequestBody StudentCourseSectionRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created("Đăng ký lớp học phần thành công", studentCourseSectionService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<StudentCourseSectionResponseDTO>>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage("Lấy danh sách đăng ký lớp học phần thành công", studentCourseSectionService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentCourseSectionResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok("Lấy thông tin đăng ký lớp học phần thành công", studentCourseSectionService.getById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        studentCourseSectionService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa đăng ký lớp học phần thành công", null));
    }
}
