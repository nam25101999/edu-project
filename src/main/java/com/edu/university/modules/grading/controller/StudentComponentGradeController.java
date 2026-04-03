package com.edu.university.modules.grading.controller;

import com.edu.university.modules.grading.dto.request.StudentComponentGradeRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentComponentGradeResponseDTO;
import com.edu.university.modules.grading.service.StudentComponentGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-component-grades")
@RequiredArgsConstructor
public class StudentComponentGradeController {

    private final StudentComponentGradeService studentComponentGradeService;

    @PostMapping
    public ResponseEntity<StudentComponentGradeResponseDTO> upsert(@Valid @RequestBody StudentComponentGradeRequestDTO requestDTO) {
        return ResponseEntity.ok(studentComponentGradeService.upsert(requestDTO));
    }

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<List<StudentComponentGradeResponseDTO>> getByRegistrationId(@PathVariable UUID registrationId) {
        return ResponseEntity.ok(studentComponentGradeService.getByRegistrationId(registrationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        studentComponentGradeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
