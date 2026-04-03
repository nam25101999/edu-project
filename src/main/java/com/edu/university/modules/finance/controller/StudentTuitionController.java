package com.edu.university.modules.finance.controller;

import com.edu.university.modules.finance.dto.request.StudentTuitionRequestDTO;
import com.edu.university.modules.finance.dto.response.StudentTuitionResponseDTO;
import com.edu.university.modules.finance.service.StudentTuitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-tuitions")
@RequiredArgsConstructor
public class StudentTuitionController {

    private final StudentTuitionService studentTuitionService;

    @PostMapping
    public ResponseEntity<StudentTuitionResponseDTO> create(@Valid @RequestBody StudentTuitionRequestDTO requestDTO) {
        return new ResponseEntity<>(studentTuitionService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentTuitionResponseDTO>> getByStudentId(@PathVariable UUID studentId) {
        return ResponseEntity.ok(studentTuitionService.getByStudentId(studentId));
    }

    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<List<StudentTuitionResponseDTO>> getBySemesterId(@PathVariable UUID semesterId) {
        return ResponseEntity.ok(studentTuitionService.getBySemesterId(semesterId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentTuitionResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody StudentTuitionRequestDTO requestDTO) {
        return ResponseEntity.ok(studentTuitionService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        studentTuitionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
