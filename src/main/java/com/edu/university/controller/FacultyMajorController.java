package com.edu.university.controller;

import com.edu.university.dto.FacultyMajorDtos.*;
import com.edu.university.service.FacultyMajorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class FacultyMajorController {

    private final FacultyMajorService service;

    // === API KHOA (FACULTY) ===
    @GetMapping("/faculties")
    public ResponseEntity<?> getAllFaculties() {
        return ResponseEntity.ok(service.getAllFaculties());
    }

    @PostMapping("/faculties")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFaculty(@Valid @RequestBody FacultyRequest request) {
        return ResponseEntity.ok(service.createFaculty(request));
    }

    // === API NGÀNH (MAJOR) ===
    @GetMapping("/majors")
    public ResponseEntity<?> getAllMajors() {
        return ResponseEntity.ok(service.getAllMajors());
    }

    @GetMapping("/faculties/{facultyId}/majors")
    public ResponseEntity<?> getMajorsByFaculty(@PathVariable UUID facultyId) {
        return ResponseEntity.ok(service.getMajorsByFaculty(facultyId));
    }

    @PostMapping("/majors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMajor(@Valid @RequestBody MajorRequest request) {
        return ResponseEntity.ok(service.createMajor(request));
    }
}