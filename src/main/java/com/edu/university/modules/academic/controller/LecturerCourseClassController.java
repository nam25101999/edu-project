package com.edu.university.modules.academic.controller;

import com.edu.university.modules.academic.dto.request.LecturerCourseClassRequestDTO;
import com.edu.university.modules.academic.dto.response.LecturerCourseClassResponseDTO;
import com.edu.university.modules.academic.service.LecturerCourseClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lecturer-course-classes")
@RequiredArgsConstructor
public class LecturerCourseClassController {

    private final LecturerCourseClassService lecturerCourseClassService;

    @PostMapping
    public ResponseEntity<LecturerCourseClassResponseDTO> create(@Valid @RequestBody LecturerCourseClassRequestDTO requestDTO) {
        return new ResponseEntity<>(lecturerCourseClassService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LecturerCourseClassResponseDTO>> getAll() {
        return ResponseEntity.ok(lecturerCourseClassService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LecturerCourseClassResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(lecturerCourseClassService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        lecturerCourseClassService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
