package com.edu.university.modules.academic.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.CourseSectionResponseDTO;
import com.edu.university.modules.academic.service.CourseSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/course-sections")
@RequiredArgsConstructor
@Validated
public class CourseSectionController {

    private final CourseSectionService courseSectionService;

    @PostMapping
    public ResponseEntity<BaseResponse<CourseSectionResponseDTO>> create(@Valid @RequestBody CourseSectionRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created("Tạo lớp học phần thành công", courseSectionService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<CourseSectionResponseDTO>>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok("Lấy danh sách lớp học phần thành công", courseSectionService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CourseSectionResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok("Lấy thông tin lớp học phần thành công", courseSectionService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CourseSectionResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody CourseSectionRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật lớp học phần thành công", courseSectionService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        courseSectionService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa lớp học phần thành công", null));
    }
}
