package com.edu.university.modules.examination.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.examination.dto.request.ExamRegistrationRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRegistrationResponseDTO;
import com.edu.university.modules.examination.service.ExamRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exam-registrations")
@RequiredArgsConstructor
public class ExamRegistrationController {

    private final ExamRegistrationService examRegistrationService;

    @PostMapping
    public ResponseEntity<BaseResponse<ExamRegistrationResponseDTO>> create(@Valid @RequestBody ExamRegistrationRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.created(examRegistrationService.create(requestDTO)));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<BaseResponse<PageResponse<ExamRegistrationResponseDTO>>> getByExamId(@PathVariable UUID examId, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(examRegistrationService.getByExamId(examId, pageable)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<BaseResponse<PageResponse<ExamRegistrationResponseDTO>>> getByStudentId(@PathVariable UUID studentId, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(examRegistrationService.getByStudentId(studentId, pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examRegistrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
