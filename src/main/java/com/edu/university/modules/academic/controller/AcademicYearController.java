package com.edu.university.modules.academic.controller;

import com.edu.university.modules.academic.dto.request.AcademicYearRequestDTO;
import com.edu.university.modules.academic.dto.response.AcademicYearResponseDTO;
import com.edu.university.modules.academic.service.AcademicYearService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @PostMapping
    public ResponseEntity<AcademicYearResponseDTO> create(@Valid @RequestBody AcademicYearRequestDTO requestDTO) {
        return new ResponseEntity<>(academicYearService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AcademicYearResponseDTO>> getAll() {
        return ResponseEntity.ok(academicYearService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicYearResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(academicYearService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcademicYearResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody AcademicYearRequestDTO requestDTO) {
        return ResponseEntity.ok(academicYearService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        academicYearService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
