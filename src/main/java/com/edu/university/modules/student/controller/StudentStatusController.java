package com.edu.university.modules.student.controller;

import com.edu.university.modules.student.dto.request.StudentStatusRequestDTO;
import com.edu.university.modules.student.dto.response.StudentStatusResponseDTO;
import com.edu.university.modules.student.service.StudentStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-status")
@RequiredArgsConstructor
public class StudentStatusController {

    private final StudentStatusService statusService;

    // 1. Thêm trạng thái mới cho sinh viên
    @PostMapping
    public ResponseEntity<StudentStatusResponseDTO> createStatus(
            @Valid @RequestBody StudentStatusRequestDTO requestDTO) {
        return new ResponseEntity<>(statusService.createStatus(requestDTO), HttpStatus.CREATED);
    }

    // 2. Lấy tất cả trạng thái
    @GetMapping
    public ResponseEntity<List<StudentStatusResponseDTO>> getAll() {
        return ResponseEntity.ok(statusService.getAll());
    }

    // 3. Lấy trạng thái sinh viên
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentStatusResponseDTO>> getByStudentId(@PathVariable UUID studentId) {
        return ResponseEntity.ok(statusService.getByStudentId(studentId));
    }

    // 4. Cập nhật trạng thái
    @PutMapping("/{id}")
    public ResponseEntity<StudentStatusResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StudentStatusRequestDTO requestDTO) {
        return ResponseEntity.ok(statusService.updateStatus(id, requestDTO));
    }

    // 5. Xóa mềm trạng thái
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable UUID id) {
        statusService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }
}