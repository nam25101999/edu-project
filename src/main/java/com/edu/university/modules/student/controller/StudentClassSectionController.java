package com.edu.university.modules.student.controller;

import com.edu.university.modules.student.dto.request.StudentClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassSectionResponseDTO;
import com.edu.university.modules.student.service.StudentClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-class-sections")
@RequiredArgsConstructor
public class StudentClassSectionController {

    private final StudentClassSectionService sectionService;

    // 1. Thêm sinh viên vào lớp
    @PostMapping
    public ResponseEntity<StudentClassSectionResponseDTO> addStudentToClass(
            @Valid @RequestBody StudentClassSectionRequestDTO requestDTO) {
        return new ResponseEntity<>(sectionService.addStudentToClass(requestDTO), HttpStatus.CREATED);
    }

    // 2. Lấy danh sách tất cả
    @GetMapping
    public ResponseEntity<List<StudentClassSectionResponseDTO>> getAll() {
        return ResponseEntity.ok(sectionService.getAll());
    }

    // 3. Lấy lớp của sinh viên
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentClassSectionResponseDTO>> getByStudentId(@PathVariable UUID studentId) {
        return ResponseEntity.ok(sectionService.getByStudentId(studentId));
    }

    // 4. Lấy danh sách sinh viên trong lớp
    @GetMapping("/class/{studentClassesId}")
    public ResponseEntity<List<StudentClassSectionResponseDTO>> getByClassId(@PathVariable UUID studentClassesId) {
        return ResponseEntity.ok(sectionService.getByClassId(studentClassesId));
    }

    // 5. Cập nhật thông tin
    @PutMapping("/{id}")
    public ResponseEntity<StudentClassSectionResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody StudentClassSectionRequestDTO requestDTO) {
        return ResponseEntity.ok(sectionService.update(id, requestDTO));
    }

    // 6. Xóa mềm sinh viên khỏi lớp
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        sectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}