package com.edu.university.modules.finance.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.finance.dto.request.StudentTuitionRequestDTO;
import com.edu.university.modules.finance.dto.response.StudentTuitionResponseDTO;
import com.edu.university.modules.finance.service.StudentTuitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/student-tuitions")
@RequiredArgsConstructor
public class StudentTuitionController {

    private final StudentTuitionService studentTuitionService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentTuitionResponseDTO>> create(@Valid @RequestBody StudentTuitionRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo học phí sinh viên thành công", studentTuitionService.create(requestDTO)), 
                HttpStatus.CREATED
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<BaseResponse<PageResponse<StudentTuitionResponseDTO>>> getByStudentId(
            @PathVariable UUID studentId, 
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(studentTuitionService.getByStudentId(studentId, pageable)));
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<BaseResponse<PageResponse<StudentTuitionResponseDTO>>> getBySemesterId(
            @PathVariable UUID semesterId, 
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(studentTuitionService.getBySemesterId(semesterId, pageable)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentTuitionResponseDTO>> update(
            @PathVariable UUID id, 
            @Valid @RequestBody StudentTuitionRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật học phí sinh viên thành công", studentTuitionService.update(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        studentTuitionService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa học phí sinh viên thành công", null));
    }
}
