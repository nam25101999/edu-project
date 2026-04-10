package com.edu.university.modules.academic.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.dto.response.SemesterResponseDTO;
import com.edu.university.modules.academic.service.SemesterService;
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
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
@Validated
public class SemesterController {

    private final SemesterService semesterService;

    @PostMapping
    public ResponseEntity<BaseResponse<SemesterResponseDTO>> create(@Valid @RequestBody SemesterRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created("Tạo học kỳ thành công", semesterService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<SemesterResponseDTO>>> getAll(
            @RequestParam(required = false) String academicYear,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage("Lấy danh sách học kỳ thành công", semesterService.getAll(academicYear, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<SemesterResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok("Lấy thông tin học kỳ thành công", semesterService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<SemesterResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody SemesterRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật học kỳ thành công", semesterService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        semesterService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa học kỳ thành công", null));
    }
}
