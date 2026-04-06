package com.edu.university.modules.registration.controller;

import com.edu.university.modules.registration.dto.request.CourseRegistrationRequestDTO;
import com.edu.university.modules.registration.dto.request.EligibilityCheckRequest;
import com.edu.university.modules.registration.dto.response.CourseRegistrationResponseDTO;
import com.edu.university.modules.registration.dto.response.EligibilityCheckResponse;
import com.edu.university.modules.registration.service.CourseRegistrationService;
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
@RequestMapping("/api/course-registrations")
@RequiredArgsConstructor
public class CourseRegistrationController {

    private final CourseRegistrationService courseRegistrationService;

    @PostMapping
    public ResponseEntity<CourseRegistrationResponseDTO> create(@Valid @RequestBody CourseRegistrationRequestDTO requestDTO) {
        return new ResponseEntity<>(courseRegistrationService.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CourseRegistrationResponseDTO>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(courseRegistrationService.getAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseRegistrationResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(courseRegistrationService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseRegistrationResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody CourseRegistrationRequestDTO requestDTO) {
        return ResponseEntity.ok(courseRegistrationService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        courseRegistrationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-eligibility")
    public ResponseEntity<EligibilityCheckResponse> checkEligibility(@RequestBody EligibilityCheckRequest request) {
        return ResponseEntity.ok(courseRegistrationService.checkEligibility(request));
    }
}
