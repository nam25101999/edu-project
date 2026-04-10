package com.edu.university.modules.finance.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.dto.response.TuitionFeeResponseDTO;
import com.edu.university.modules.finance.service.TuitionFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tuition-fees")
@RequiredArgsConstructor
public class TuitionFeeController {

    private final TuitionFeeService tuitionFeeService;

    @PostMapping
    public ResponseEntity<BaseResponse<TuitionFeeResponseDTO>> create(@Valid @RequestBody TuitionFeeRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created("Tạo định mức học phí thành công", tuitionFeeService.create(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<TuitionFeeResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(tuitionFeeService.getAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<TuitionFeeResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(tuitionFeeService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<TuitionFeeResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody TuitionFeeRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật định mức học phí thành công", tuitionFeeService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        tuitionFeeService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa định mức học phí thành công", null));
    }
}
