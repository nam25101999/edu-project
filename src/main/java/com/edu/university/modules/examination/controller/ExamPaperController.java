package com.edu.university.modules.examination.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.examination.dto.request.ExamPaperRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamPaperResponseDTO;
import com.edu.university.modules.examination.service.ExamPaperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exam-papers")
@RequiredArgsConstructor
public class ExamPaperController {

    private final ExamPaperService examPaperService;

    @PostMapping
    public ResponseEntity<BaseResponse<ExamPaperResponseDTO>> create(@Valid @RequestBody ExamPaperRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.created(examPaperService.create(requestDTO)));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<BaseResponse<PageResponse<ExamPaperResponseDTO>>> getByExamId(@PathVariable UUID examId, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(examPaperService.getByExamId(examId, pageable)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable UUID id) {
        examPaperService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok());
    }
}
