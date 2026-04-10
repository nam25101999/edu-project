package com.edu.university.modules.curriculum.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.curriculum.dto.request.CourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CourseResponseDTO;
import com.edu.university.modules.curriculum.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<BaseResponse<CourseResponseDTO>> create(@Valid @RequestBody CourseRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo môn học thành công", courseService.create(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<CourseResponseDTO>>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.okPage(courseService.getAll(search, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CourseResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(courseService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CourseResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody CourseRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật môn học thành công", courseService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        courseService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa môn học thành công", null));
    }
}
