package com.edu.university.modules.academic.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.academic.dto.request.AcademicYearRequestDTO;
import com.edu.university.modules.academic.dto.response.AcademicYearResponseDTO;
import com.edu.university.modules.academic.service.AcademicYearService;
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
@RequestMapping("/api/academic-years")
@RequiredArgsConstructor
@Validated
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @PostMapping
    public ResponseEntity<BaseResponse<AcademicYearResponseDTO>> create(@Valid @RequestBody AcademicYearRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created("Tạo năm học thành công", academicYearService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<AcademicYearResponseDTO>>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok("Lấy danh sách năm học thành công", academicYearService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AcademicYearResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok("Lấy thông tin năm học thành công", academicYearService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AcademicYearResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody AcademicYearRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật năm học thành công", academicYearService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        academicYearService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa năm học thành công", null));
    }
}
