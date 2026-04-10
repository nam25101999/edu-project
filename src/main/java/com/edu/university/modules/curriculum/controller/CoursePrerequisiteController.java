package com.edu.university.modules.curriculum.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.curriculum.dto.request.CoursePrerequisiteRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CoursePrerequisiteResponseDTO;
import com.edu.university.modules.curriculum.service.CoursePrerequisiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/course-prerequisites")
@RequiredArgsConstructor
public class CoursePrerequisiteController {

    private final CoursePrerequisiteService coursePrerequisiteService;

    @PostMapping
    public ResponseEntity<BaseResponse<CoursePrerequisiteResponseDTO>> create(@Valid @RequestBody CoursePrerequisiteRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo môn tiên quyết thành công", coursePrerequisiteService.create(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<CoursePrerequisiteResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(coursePrerequisiteService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CoursePrerequisiteResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(coursePrerequisiteService.getById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        coursePrerequisiteService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa môn tiên quyết thành công", null));
    }
}
