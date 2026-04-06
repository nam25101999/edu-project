package com.edu.university.modules.student.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.student.dto.request.StudentClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassSectionResponseDTO;
import com.edu.university.modules.student.service.StudentClassSectionService;
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
@RequestMapping("/api/student-class-sections")
@RequiredArgsConstructor
public class StudentClassSectionController {

    private final StudentClassSectionService sectionService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentClassSectionResponseDTO>> addStudentToClass(
            @Valid @RequestBody StudentClassSectionRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Thêm sinh viên vào lớp thành công", sectionService.addStudentToClass(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<StudentClassSectionResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(sectionService.getAll(pageable)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<BaseResponse<Page<StudentClassSectionResponseDTO>>> getByStudentId(
            @PathVariable UUID studentId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(sectionService.getByStudentId(studentId, pageable)));
    }

    @GetMapping("/class/{studentClassesId}")
    public ResponseEntity<BaseResponse<Page<StudentClassSectionResponseDTO>>> getByClassId(
            @PathVariable UUID studentClassesId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(sectionService.getByClassId(studentClassesId, pageable)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentClassSectionResponseDTO>> update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentClassSectionRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật thông tin thành công", sectionService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        sectionService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa sinh viên khỏi lớp thành công", null));
    }
}