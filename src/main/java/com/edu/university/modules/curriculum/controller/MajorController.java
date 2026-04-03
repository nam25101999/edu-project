package com.edu.university.modules.curriculum.controller;

import com.edu.university.modules.curriculum.dto.request.MajorRequestDTO;
import com.edu.university.modules.curriculum.dto.response.MajorResponseDTO;
import com.edu.university.modules.curriculum.service.MajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/majors")
@RequiredArgsConstructor
public class MajorController {

    private final MajorService majorService;

    @PostMapping
    public ResponseEntity<MajorResponseDTO> create(@Valid @RequestBody MajorRequestDTO requestDTO) {
        return new ResponseEntity<>(majorService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MajorResponseDTO>> getAll() {
        return ResponseEntity.ok(majorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MajorResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(majorService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MajorResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody MajorRequestDTO requestDTO) {
        return ResponseEntity.ok(majorService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        majorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
