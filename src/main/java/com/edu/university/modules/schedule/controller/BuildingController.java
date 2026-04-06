package com.edu.university.modules.schedule.controller;

import com.edu.university.modules.schedule.dto.request.BuildingRequestDTO;
import com.edu.university.modules.schedule.dto.response.BuildingResponseDTO;
import com.edu.university.modules.schedule.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping
    public ResponseEntity<BuildingResponseDTO> create(@Valid @RequestBody BuildingRequestDTO requestDTO) {
        return new ResponseEntity<>(buildingService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BuildingResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(buildingService.getAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(buildingService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BuildingResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody BuildingRequestDTO requestDTO) {
        return ResponseEntity.ok(buildingService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        buildingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
