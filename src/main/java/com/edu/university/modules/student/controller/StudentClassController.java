package com.edu.university.modules.student.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassResponseDTO;
import com.edu.university.modules.student.service.StudentClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/student-classes")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassService studentClassService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentClassResponseDTO>> createClass(@Valid @RequestBody StudentClassRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Táº¡o lá»›p há» c thÃ nh cÃ´ng", studentClassService.createClass(requestDTO)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StudentClassResponseDTO>>> getClasses(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID majorId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(studentClassService.getClassesByDepartmentAndMajor(departmentId, majorId, pageable))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentClassResponseDTO>> getClassById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(studentClassService.getClassById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentClassResponseDTO>> updateClass(
            @PathVariable UUID id,
            @Valid @RequestBody StudentClassRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success("Cáº­p nháº­t lá»›p há» c thÃ nh cÃ´ng", studentClassService.updateClass(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable UUID id) {
        studentClassService.deleteClass(id);
        return ResponseEntity.ok(ApiResponse.success("XÃ³a lá»›p há» c thÃ nh cÃ´ng", null));
    }
}