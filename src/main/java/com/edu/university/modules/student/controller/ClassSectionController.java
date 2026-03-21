package com.edu.university.modules.student.controller;

import com.edu.university.modules.enrollment.repository.course.dto.ClassSectionDtos.ClassSectionRequest;
import com.edu.university.modules.enrollment.service.service.ClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassSectionController {

    private final ClassSectionService classSectionService;

    @GetMapping
    public ResponseEntity<?> getAllClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(classSectionService.getAllClassSections(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable UUID id) {
        return ResponseEntity.ok(classSectionService.getClassSectionById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClass(@Valid @RequestBody ClassSectionRequest request) {
        return ResponseEntity.ok(classSectionService.createClassSection(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateClass(@PathVariable UUID id, @Valid @RequestBody ClassSectionRequest request) {
        return ResponseEntity.ok(classSectionService.updateClassSection(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable UUID id) {
        classSectionService.deleteClassSection(id);
        return ResponseEntity.ok("Xóa lớp học phần thành công");
    }
}