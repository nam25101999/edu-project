package com.edu.university.modules.curriculum.controller;

import com.edu.university.modules.curriculum.dto.request.TrainingProgramCourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramCourseResponseDTO;
import com.edu.university.modules.curriculum.service.TrainingProgramCourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/training-program-courses")
@RequiredArgsConstructor
public class TrainingProgramCourseController {

    private final TrainingProgramCourseService trainingProgramCourseService;

    @PostMapping
    public ResponseEntity<TrainingProgramCourseResponseDTO> create(@Valid @RequestBody TrainingProgramCourseRequestDTO requestDTO) {
        return new ResponseEntity<>(trainingProgramCourseService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TrainingProgramCourseResponseDTO>> getAll() {
        return ResponseEntity.ok(trainingProgramCourseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingProgramCourseResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(trainingProgramCourseService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingProgramCourseResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody TrainingProgramCourseRequestDTO requestDTO) {
        return ResponseEntity.ok(trainingProgramCourseService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        trainingProgramCourseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
