package com.edu.university.modules.academic.controller;

import com.edu.university.modules.academic.dto.request.StudentCourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.StudentCourseSectionResponseDTO;
import com.edu.university.modules.academic.service.StudentCourseSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-course-sections")
@RequiredArgsConstructor
public class StudentCourseSectionController {

    private final StudentCourseSectionService studentCourseSectionService;

    @PostMapping
    public ResponseEntity<StudentCourseSectionResponseDTO> create(@Valid @RequestBody StudentCourseSectionRequestDTO requestDTO) {
        return new ResponseEntity<>(studentCourseSectionService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentCourseSectionResponseDTO>> getAll() {
        return ResponseEntity.ok(studentCourseSectionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentCourseSectionResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentCourseSectionService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        studentCourseSectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
