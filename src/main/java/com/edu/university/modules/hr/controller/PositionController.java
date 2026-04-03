package com.edu.university.modules.hr.controller;

import com.edu.university.modules.hr.dto.request.PositionRequestDTO;
import com.edu.university.modules.hr.dto.response.PositionResponseDTO;
import com.edu.university.modules.hr.service.PositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    public ResponseEntity<PositionResponseDTO> createPosition(@Valid @RequestBody PositionRequestDTO requestDTO) {
        return new ResponseEntity<>(positionService.createPosition(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PositionResponseDTO>> getAllPositions() {
        return ResponseEntity.ok(positionService.getAllPositions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionResponseDTO> getPositionById(@PathVariable UUID id) {
        return ResponseEntity.ok(positionService.getPositionById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PositionResponseDTO> getPositionByCode(@PathVariable String code) {
        return ResponseEntity.ok(positionService.getPositionByCode(code));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionResponseDTO> updatePosition(
            @PathVariable UUID id,
            @Valid @RequestBody PositionRequestDTO requestDTO) {
        return ResponseEntity.ok(positionService.updatePosition(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable UUID id) {
        positionService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }
}
