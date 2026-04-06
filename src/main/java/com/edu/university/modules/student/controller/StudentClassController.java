package com.edu.university.modules.student.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassResponseDTO;
import com.edu.university.modules.student.service.StudentClassService;
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
@RequestMapping("/api/student-classes")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassService studentClassService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentClassResponseDTO>> createClass(@Valid @RequestBody StudentClassRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo lớp thành công", studentClassService.createClass(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<StudentClassResponseDTO>>> getClasses(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID majorId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(studentClassService.getClassesByDepartmentAndMajor(departmentId, majorId, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentClassResponseDTO>> getClassById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(studentClassService.getClassById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentClassResponseDTO>> updateClass(
            @PathVariable UUID id,
            @Valid @RequestBody StudentClassRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật lớp thành công", studentClassService.updateClass(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteClass(@PathVariable UUID id) {
        studentClassService.deleteClass(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa lớp thành công", null));
    }
}