package com.edu.university.modules.examination.controller;

import com.edu.university.modules.examination.dto.request.ExamRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResponseDTO;
import com.edu.university.modules.examination.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<ExamResponseDTO> create(@Valid @RequestBody ExamRequestDTO requestDTO) {
        return new ResponseEntity<>(examService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ExamResponseDTO>> getAll() {
        return ResponseEntity.ok(examService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(examService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody ExamRequestDTO requestDTO) {
        return ResponseEntity.ok(examService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
