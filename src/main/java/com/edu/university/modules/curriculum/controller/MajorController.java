package com.edu.university.modules.curriculum.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.curriculum.dto.request.MajorRequestDTO;
import com.edu.university.modules.curriculum.dto.response.MajorResponseDTO;
import com.edu.university.modules.curriculum.service.MajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/majors")
@RequiredArgsConstructor
public class MajorController {

    private final MajorService majorService;

    @PostMapping
    public ResponseEntity<BaseResponse<MajorResponseDTO>> create(@Valid @RequestBody MajorRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo ngành học thành công", majorService.create(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<MajorResponseDTO>>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.okPage(majorService.getAll(search, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<MajorResponseDTO>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(majorService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<MajorResponseDTO>> update(@PathVariable UUID id, @Valid @RequestBody MajorRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật ngành học thành công", majorService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        majorService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa ngành học thành công", null));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<BaseResponse<java.util.List<MajorResponseDTO>>> getByDepartment(@PathVariable UUID departmentId) {
        return ResponseEntity.ok(BaseResponse.ok(majorService.getByDepartment(departmentId)));
    }
}
