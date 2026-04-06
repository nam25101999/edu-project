package com.edu.university.modules.grading.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.grading.dto.request.GradeComponentRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeComponentResponseDTO;
import com.edu.university.modules.grading.service.GradeComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/grade-components")
@RequiredArgsConstructor
public class GradeComponentController {

    private final GradeComponentService gradeComponentService;

    @PostMapping
    public ResponseEntity<BaseResponse<GradeComponentResponseDTO>> create(@Valid @RequestBody GradeComponentRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo thành phần điểm thành công", gradeComponentService.create(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/course-section/{courseSectionId}")
    public ResponseEntity<BaseResponse<Page<GradeComponentResponseDTO>>> getByCourseSectionId(
            @PathVariable UUID courseSectionId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(gradeComponentService.getByCourseSectionId(courseSectionId, pageable)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<GradeComponentResponseDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody GradeComponentRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật thành phần điểm thành công", gradeComponentService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        gradeComponentService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa thành phần điểm thành công", null));
    }
}
