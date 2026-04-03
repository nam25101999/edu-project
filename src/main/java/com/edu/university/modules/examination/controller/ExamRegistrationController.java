package com.edu.university.modules.examination.controller;

import com.edu.university.modules.examination.dto.request.ExamRegistrationRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRegistrationResponseDTO;
import com.edu.university.modules.examination.service.ExamRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/exam-registrations")
@RequiredArgsConstructor
public class ExamRegistrationController {

    private final ExamRegistrationService examRegistrationService;

    @PostMapping
    public ResponseEntity<ExamRegistrationResponseDTO> create(@Valid @RequestBody ExamRegistrationRequestDTO requestDTO) {
        return new ResponseEntity<>(examRegistrationService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ExamRegistrationResponseDTO>> getByExamId(@PathVariable UUID examId) {
        return ResponseEntity.ok(examRegistrationService.getByExamId(examId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ExamRegistrationResponseDTO>> getByStudentId(@PathVariable UUID studentId) {
        return ResponseEntity.ok(examRegistrationService.getByStudentId(studentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examRegistrationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
