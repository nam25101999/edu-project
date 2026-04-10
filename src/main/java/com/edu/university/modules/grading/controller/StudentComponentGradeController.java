package com.edu.university.modules.grading.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.grading.dto.request.StudentComponentGradeRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentComponentGradeResponseDTO;
import com.edu.university.modules.grading.service.StudentComponentGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/student-component-grades")
@RequiredArgsConstructor
public class StudentComponentGradeController {

    private final StudentComponentGradeService studentComponentGradeService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentComponentGradeResponseDTO>> upsert(@Valid @RequestBody StudentComponentGradeRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật điểm thành phần thành công", studentComponentGradeService.upsert(requestDTO)));
    }

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<BaseResponse<PageResponse<StudentComponentGradeResponseDTO>>> getByRegistrationId(
            @PathVariable UUID registrationId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(studentComponentGradeService.getByRegistrationId(registrationId, pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        studentComponentGradeService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa điểm thành phần thành công", null));
    }
}
