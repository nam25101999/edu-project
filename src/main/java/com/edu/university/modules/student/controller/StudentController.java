package com.edu.university.modules.student.controller;

import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
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

    // --- LẤY DANH SÁCH SINH VIÊN (Phân trang) ---
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Student>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(studentRepository.findAll(PageRequest.of(page, size)));
    }

    // --- XEM CHI TIẾT 1 SINH VIÊN THEO ID (Read) ---
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Student> getStudentById(@PathVariable UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return ResponseEntity.ok(student);
    }

    // --- THÊM SINH VIÊN MỚI (Create) ---
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        // Lưu ý: Đảm bảo phía client không gửi kèm ID để database tự sinh (tuỳ chiến lược generate ID)
        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    // --- CẬP NHẬT THÔNG TIN SINH VIÊN (Update) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Student> updateStudent(@PathVariable UUID id, @RequestBody Student studentDetails) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        // TODO: Thay đổi các trường bên dưới cho khớp với Entity Student của bạn
        // existingStudent.setName(studentDetails.getName());
        // existingStudent.setEmail(studentDetails.getEmail());
        // existingStudent.setPhone(studentDetails.getPhone());
        // ... (Không cập nhật trường id)

        Student updatedStudent = studentRepository.save(existingStudent);
        return ResponseEntity.ok(updatedStudent);
    }

    // --- XÓA SINH VIÊN (Delete) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteStudent(@PathVariable UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        studentRepository.delete(student);
        return ResponseEntity.ok("Student deleted successfully.");
    }

    // --- UPLOAD AVATAR (Có sẵn) ---
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