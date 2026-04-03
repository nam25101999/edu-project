package com.edu.university.modules.academic.controller;

import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.dto.response.SemesterResponseDTO;
import com.edu.university.modules.academic.service.SemesterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterService semesterService;

    @PostMapping
    public ResponseEntity<SemesterResponseDTO> create(@Valid @RequestBody SemesterRequestDTO requestDTO) {
        return new ResponseEntity<>(semesterService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SemesterResponseDTO>> getAll() {
        return ResponseEntity.ok(semesterService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SemesterResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(semesterService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SemesterResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody SemesterRequestDTO requestDTO) {
        return ResponseEntity.ok(semesterService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        semesterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
