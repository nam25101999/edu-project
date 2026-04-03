package com.edu.university.modules.grading.controller;

import com.edu.university.modules.grading.dto.request.GradeScaleRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeScaleResponseDTO;
import com.edu.university.modules.grading.service.GradeScaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grade-scales")
@RequiredArgsConstructor
public class GradeScaleController {

    private final GradeScaleService gradeScaleService;

    @PostMapping
    public ResponseEntity<GradeScaleResponseDTO> create(@Valid @RequestBody GradeScaleRequestDTO requestDTO) {
        return new ResponseEntity<>(gradeScaleService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GradeScaleResponseDTO>> getAll() {
        return ResponseEntity.ok(gradeScaleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeScaleResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(gradeScaleService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeScaleResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody GradeScaleRequestDTO requestDTO) {
        return ResponseEntity.ok(gradeScaleService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        gradeScaleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
