package com.edu.university.modules.registration.controller;

import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.dto.response.RegistrationPeriodResponseDTO;
import com.edu.university.modules.registration.service.RegistrationPeriodService;
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
@RequestMapping("/api/registration-periods")
@RequiredArgsConstructor
public class RegistrationPeriodController {

    private final RegistrationPeriodService registrationPeriodService;

    @PostMapping
    public ResponseEntity<RegistrationPeriodResponseDTO> create(@Valid @RequestBody RegistrationPeriodRequestDTO requestDTO) {
        return new ResponseEntity<>(registrationPeriodService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RegistrationPeriodResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(registrationPeriodService.getAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationPeriodResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(registrationPeriodService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistrationPeriodResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody RegistrationPeriodRequestDTO requestDTO) {
        return ResponseEntity.ok(registrationPeriodService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        registrationPeriodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
