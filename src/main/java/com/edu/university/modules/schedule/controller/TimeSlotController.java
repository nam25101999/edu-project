package com.edu.university.modules.schedule.controller;

import com.edu.university.modules.schedule.dto.request.TimeSlotRequestDTO;
import com.edu.university.modules.schedule.dto.response.TimeSlotResponseDTO;
import com.edu.university.modules.schedule.service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    @PostMapping
    public ResponseEntity<TimeSlotResponseDTO> create(@Valid @RequestBody TimeSlotRequestDTO requestDTO) {
        return new ResponseEntity<>(timeSlotService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TimeSlotResponseDTO>> getAll() {
        return ResponseEntity.ok(timeSlotService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSlotResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(timeSlotService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeSlotResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody TimeSlotRequestDTO requestDTO) {
        return ResponseEntity.ok(timeSlotService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        timeSlotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
