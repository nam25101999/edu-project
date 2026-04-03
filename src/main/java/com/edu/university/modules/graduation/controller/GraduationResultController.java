package com.edu.university.modules.graduation.controller;

import com.edu.university.modules.graduation.dto.request.GraduationResultRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationResultResponseDTO;
import com.edu.university.modules.graduation.service.GraduationResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/graduation-results")
@RequiredArgsConstructor
public class GraduationResultController {

    private final GraduationResultService graduationResultService;

    @PostMapping
    public ResponseEntity<GraduationResultResponseDTO> create(@Valid @RequestBody GraduationResultRequestDTO requestDTO) {
        return new ResponseEntity<>(graduationResultService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GraduationResultResponseDTO>> getByStudentId(@PathVariable UUID studentId) {
        return ResponseEntity.ok(graduationResultService.getByStudentId(studentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        graduationResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
