package com.edu.university.modules.student.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.request.StudentStatusChangeRequestDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentResponseDTO>> createStudent(@Valid @RequestBody StudentRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo sinh viên thành công", studentService.createStudent(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<StudentResponseDTO>>> getAllStudents(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(studentService.getAllStudents(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentResponseDTO>> getStudentById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(studentService.getStudentById(id)));
    }

    @GetMapping("/code/{studentCode}")
    public ResponseEntity<BaseResponse<StudentResponseDTO>> getStudentByCode(@PathVariable String studentCode) {
        return ResponseEntity.ok(BaseResponse.ok(studentService.getStudentByCode(studentCode)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentResponseDTO>> updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody StudentRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật sinh viên thành công", studentService.updateStudent(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa sinh viên thành công", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<StudentResponseDTO>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StudentStatusChangeRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Thay đổi trạng thái sinh viên thành công", studentService.changeStatus(id, requestDTO)));
    }
}