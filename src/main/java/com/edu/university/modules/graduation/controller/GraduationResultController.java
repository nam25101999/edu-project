package com.edu.university.modules.graduation.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.graduation.dto.request.GraduationResultRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationResultResponseDTO;
import com.edu.university.modules.graduation.service.GraduationResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/graduation-results")
@RequiredArgsConstructor
public class GraduationResultController {

    private final GraduationResultService graduationResultService;

    @PostMapping
    public ResponseEntity<BaseResponse<GraduationResultResponseDTO>> create(@Valid @RequestBody GraduationResultRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.created(graduationResultService.create(requestDTO)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<BaseResponse<PageResponse<GraduationResultResponseDTO>>> getByStudentId(@PathVariable UUID studentId, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(graduationResultService.getByStudentId(studentId, pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        graduationResultService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok());
    }
}
