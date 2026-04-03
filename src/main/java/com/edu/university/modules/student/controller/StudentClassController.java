package com.edu.university.modules.student.controller;

import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassResponseDTO;
import com.edu.university.modules.student.service.StudentClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-classes")
@RequiredArgsConstructor
public class StudentClassController {

    private final StudentClassService studentClassService;

    // 1. Tạo lớp mới
    @PostMapping
    public ResponseEntity<StudentClassResponseDTO> createClass(@Valid @RequestBody StudentClassRequestDTO requestDTO) {
        return new ResponseEntity<>(studentClassService.createClass(requestDTO), HttpStatus.CREATED);
    }

    // 2 & 6. Lấy danh sách lớp & Lọc lớp theo khoa, ngành
    @GetMapping
    public ResponseEntity<List<StudentClassResponseDTO>> getClasses(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID majorId) {
        return ResponseEntity.ok(studentClassService.getClassesByDepartmentAndMajor(departmentId, majorId));
    }

    // 3. Lấy chi tiết lớp
    @GetMapping("/{id}")
    public ResponseEntity<StudentClassResponseDTO> getClassById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentClassService.getClassById(id));
    }

    // 4. Cập nhật thông tin lớp
    @PutMapping("/{id}")
    public ResponseEntity<StudentClassResponseDTO> updateClass(
            @PathVariable UUID id,
            @Valid @RequestBody StudentClassRequestDTO requestDTO) {
        return ResponseEntity.ok(studentClassService.updateClass(id, requestDTO));
    }

    // 5. Xóa mềm lớp
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable UUID id) {
        studentClassService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }
}