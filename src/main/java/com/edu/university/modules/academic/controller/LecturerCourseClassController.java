package com.edu.university.modules.academic.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.academic.dto.request.LecturerCourseClassRequestDTO;
import com.edu.university.modules.academic.dto.response.LecturerCourseClassResponseDTO;
import com.edu.university.modules.academic.service.LecturerCourseClassService;
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
@RequestMapping("/api/lecturer-course-classes")
@RequiredArgsConstructor
@Validated
public class LecturerCourseClassController {

    private final LecturerCourseClassService lecturerCourseClassService;

    @PostMapping
    public ResponseEntity<BaseResponse<LecturerCourseClassResponseDTO>> create(@Valid @RequestBody LecturerCourseClassRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created("Phân công giảng viên thành công", lecturerCourseClassService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<LecturerCourseClassResponseDTO>>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok("Lấy danh sách phân công giảng viên thành công", lecturerCourseClassService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LecturerCourseClassResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok("Lấy thông tin phân công giảng viên thành công", lecturerCourseClassService.getById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        lecturerCourseClassService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa phân công giảng viên thành công", null));
    }
}
