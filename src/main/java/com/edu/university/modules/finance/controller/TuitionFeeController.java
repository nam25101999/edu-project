package com.edu.university.modules.finance.controller;

import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.dto.response.TuitionFeeResponseDTO;
import com.edu.university.modules.finance.service.TuitionFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tuition-fees")
@RequiredArgsConstructor
public class TuitionFeeController {

    private final TuitionFeeService tuitionFeeService;

    @PostMapping
    public ResponseEntity<TuitionFeeResponseDTO> create(@Valid @RequestBody TuitionFeeRequestDTO requestDTO) {
        return new ResponseEntity<>(tuitionFeeService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TuitionFeeResponseDTO>> getAll() {
        return ResponseEntity.ok(tuitionFeeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TuitionFeeResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(tuitionFeeService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TuitionFeeResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody TuitionFeeRequestDTO requestDTO) {
        return ResponseEntity.ok(tuitionFeeService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tuitionFeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
