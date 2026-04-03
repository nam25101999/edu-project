package com.edu.university.modules.grading.controller;

import com.edu.university.modules.grading.dto.request.GradeComponentRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeComponentResponseDTO;
import com.edu.university.modules.grading.service.GradeComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grade-components")
@RequiredArgsConstructor
public class GradeComponentController {

    private final GradeComponentService gradeComponentService;

    @PostMapping
    public ResponseEntity<GradeComponentResponseDTO> create(@Valid @RequestBody GradeComponentRequestDTO requestDTO) {
        return new ResponseEntity<>(gradeComponentService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/course-section/{courseSectionId}")
    public ResponseEntity<List<GradeComponentResponseDTO>> getByCourseSectionId(@PathVariable UUID courseSectionId) {
        return ResponseEntity.ok(gradeComponentService.getByCourseSectionId(courseSectionId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeComponentResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody GradeComponentRequestDTO requestDTO) {
        return ResponseEntity.ok(gradeComponentService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        gradeComponentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
