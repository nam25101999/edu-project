package com.edu.university.controller;

import com.edu.university.entity.Student;
import com.edu.university.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Student>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studentRepository.findAll(PageRequest.of(page, size)));
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> uploadAvatar(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        try {
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = id.toString() + "_" + file.getOriginalFilename();
            file.transferTo(new File(uploadDir + filename));

            student.setAvatarUrl("/" + uploadDir + filename);
            studentRepository.save(student);

            return ResponseEntity.ok("Avatar uploaded successfully: " + student.getAvatarUrl());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}