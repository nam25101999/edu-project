package com.edu.university.modules.curriculum.controller;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramResponseDTO;
import com.edu.university.modules.curriculum.service.TrainingProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/training-programs")
@RequiredArgsConstructor
public class TrainingProgramController {

    private final TrainingProgramService trainingProgramService;

    @PostMapping
    public ResponseEntity<TrainingProgramResponseDTO> create(@Valid @RequestBody TrainingProgramRequestDTO requestDTO) {
        return new ResponseEntity<>(trainingProgramService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TrainingProgramResponseDTO>> getAll() {
        return ResponseEntity.ok(trainingProgramService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingProgramResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(trainingProgramService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingProgramResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody TrainingProgramRequestDTO requestDTO) {
        return ResponseEntity.ok(trainingProgramService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        trainingProgramService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
