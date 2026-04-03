package com.edu.university.modules.examination.controller;

import com.edu.university.modules.examination.dto.request.ExamPaperRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamPaperResponseDTO;
import com.edu.university.modules.examination.service.ExamPaperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exam-papers")
@RequiredArgsConstructor
public class ExamPaperController {

    private final ExamPaperService examPaperService;

    @PostMapping
    public ResponseEntity<ExamPaperResponseDTO> create(@Valid @RequestBody ExamPaperRequestDTO requestDTO) {
        return new ResponseEntity<>(examPaperService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamPaperResponseDTO>> getByExamId(@PathVariable UUID examId) {
        return ResponseEntity.ok(examPaperService.getByExamId(examId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examPaperService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
