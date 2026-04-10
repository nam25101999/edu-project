package com.edu.university.modules.student.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.student.dto.request.AdvisorClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.AdvisorClassSectionResponseDTO;
import com.edu.university.modules.student.service.AdvisorClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/advisor-class-sections")
@RequiredArgsConstructor
public class AdvisorClassSectionController {

    private final AdvisorClassSectionService advisorService;

    @PostMapping
    public ResponseEntity<BaseResponse<AdvisorClassSectionResponseDTO>> assignAdvisorToClass(
            @Valid @RequestBody AdvisorClassSectionRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Gán cố vấn cho lớp thành công", advisorService.assignAdvisorToClass(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<AdvisorClassSectionResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(advisorService.getAll(pageable)));
    }

    @GetMapping("/advisor/{advisorId}")
    public ResponseEntity<BaseResponse<PageResponse<AdvisorClassSectionResponseDTO>>> getByAdvisorId(
            @PathVariable UUID advisorId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(advisorService.getByAdvisorId(advisorId, pageable)));
    }

    @GetMapping("/class/{studentClassesId}")
    public ResponseEntity<BaseResponse<PageResponse<AdvisorClassSectionResponseDTO>>> getByClassId(
            @PathVariable UUID studentClassesId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(advisorService.getByClassId(studentClassesId, pageable)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AdvisorClassSectionResponseDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AdvisorClassSectionRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật thông tin cố vấn thành công", advisorService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        advisorService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa phân công cố vấn thành công", null));
    }
}