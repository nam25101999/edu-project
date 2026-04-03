package com.edu.university.modules.academic.controller;

import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.CourseSectionResponseDTO;
import com.edu.university.modules.academic.service.CourseSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course-sections")
@RequiredArgsConstructor
public class CourseSectionController {

    private final CourseSectionService courseSectionService;

    @PostMapping
    public ResponseEntity<CourseSectionResponseDTO> create(@Valid @RequestBody CourseSectionRequestDTO requestDTO) {
        return new ResponseEntity<>(courseSectionService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CourseSectionResponseDTO>> getAll() {
        return ResponseEntity.ok(courseSectionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseSectionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseSectionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseSectionResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody CourseSectionRequestDTO requestDTO) {
        return ResponseEntity.ok(courseSectionService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseSectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
