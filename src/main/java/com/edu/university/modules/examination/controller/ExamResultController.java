package com.edu.university.modules.examination.controller;

import com.edu.university.modules.examination.dto.request.ExamResultRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamResultResponseDTO;
import com.edu.university.modules.examination.service.ExamResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/exam-results")
@RequiredArgsConstructor
public class ExamResultController {

    private final ExamResultService examResultService;

    @PostMapping
    public ResponseEntity<ExamResultResponseDTO> upsert(@Valid @RequestBody ExamResultRequestDTO requestDTO) {
        return ResponseEntity.ok(examResultService.upsert(requestDTO));
    }

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<ExamResultResponseDTO> getByRegistrationId(@PathVariable UUID registrationId) {
        return ResponseEntity.ok(examResultService.getByRegistrationId(registrationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
