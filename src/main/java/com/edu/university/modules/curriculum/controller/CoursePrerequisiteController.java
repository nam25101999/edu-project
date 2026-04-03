package com.edu.university.modules.curriculum.controller;

import com.edu.university.modules.curriculum.dto.request.CoursePrerequisiteRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CoursePrerequisiteResponseDTO;
import com.edu.university.modules.curriculum.service.CoursePrerequisiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course-prerequisites")
@RequiredArgsConstructor
public class CoursePrerequisiteController {

    private final CoursePrerequisiteService coursePrerequisiteService;

    @PostMapping
    public ResponseEntity<CoursePrerequisiteResponseDTO> create(@Valid @RequestBody CoursePrerequisiteRequestDTO requestDTO) {
        return new ResponseEntity<>(coursePrerequisiteService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CoursePrerequisiteResponseDTO>> getAll() {
        return ResponseEntity.ok(coursePrerequisiteService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoursePrerequisiteResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(coursePrerequisiteService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        coursePrerequisiteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
