package com.edu.university.modules.registration.controller;

import com.edu.university.modules.registration.dto.request.EquivalentCourseRequestDTO;
import com.edu.university.modules.registration.dto.response.EquivalentCourseResponseDTO;
import com.edu.university.modules.registration.service.EquivalentCourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/equivalent-courses")
@RequiredArgsConstructor
public class EquivalentCourseController {

    private final EquivalentCourseService equivalentCourseService;

    @PostMapping
    public ResponseEntity<EquivalentCourseResponseDTO> create(@Valid @RequestBody EquivalentCourseRequestDTO requestDTO) {
        return new ResponseEntity<>(equivalentCourseService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EquivalentCourseResponseDTO>> getAll() {
        return ResponseEntity.ok(equivalentCourseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquivalentCourseResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(equivalentCourseService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        equivalentCourseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
