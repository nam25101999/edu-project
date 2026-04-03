package com.edu.university.modules.student.controller;

import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.request.StudentStatusChangeRequestDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // 1. Tạo sinh viên mới
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO requestDTO) {
        return new ResponseEntity<>(studentService.createStudent(requestDTO), HttpStatus.CREATED);
    }

    // 2. Lấy danh sách tất cả sinh viên
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    // 3. Lấy thông tin chi tiết sinh viên theo ID
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // 4. Lấy sinh viên theo mã sinh viên
    @GetMapping("/code/{studentCode}")
    public ResponseEntity<StudentResponseDTO> getStudentByCode(@PathVariable String studentCode) {
        return ResponseEntity.ok(studentService.getStudentByCode(studentCode));
    }

    // 5. Cập nhật thông tin sinh viên
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        return ResponseEntity.ok(studentService.updateStudent(id, requestDTO));
    }

    // 6. Xóa mềm sinh viên
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // 7. Thay đổi trạng thái hiệu lực (is_active)
    @PatchMapping("/{id}/status")
    public ResponseEntity<StudentResponseDTO> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StudentStatusChangeRequestDTO requestDTO) {
        return ResponseEntity.ok(studentService.changeStatus(id, requestDTO));
    }
}