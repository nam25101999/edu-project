package com.edu.university.modules.student.controller;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Controller quản lý thông tin sinh viên.
 * Sử dụng ApiResponse và BusinessException chuẩn hóa với ErrorCode chi tiết.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentRepository studentRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<Student>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(studentRepository.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'LECTURER')")
    public ApiResponse<Student> getStudentById(@PathVariable UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        return ApiResponse.success(student);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Student> createStudent(@RequestBody Student student) {
        if (student.getStudentCode() != null && studentRepository.existsByStudentCode(student.getStudentCode())) {
            throw new BusinessException(ErrorCode.STUDENT_CODE_EXISTS);
        }
        return ApiResponse.created("Tạo sinh viên thành công", studentRepository.save(student));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Student> updateStudent(@PathVariable UUID id, @RequestBody Student studentDetails) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        // Cập nhật các thông tin cơ bản
        existingStudent.setFullName(studentDetails.getFullName());
        // existingStudent.setPhone(studentDetails.getPhone());
        // Cập nhật các trường khác tương ứng với Entity Student

        return ApiResponse.success("Cập nhật thông tin thành công", studentRepository.save(existingStudent));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteStudent(@PathVariable UUID id) {
        if (!studentRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }
        studentRepository.deleteById(id);
        return ApiResponse.success("Xóa sinh viên thành công", null);
    }

    @PostMapping("/{id}/avatar")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ApiResponse<String> uploadAvatar(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "File không được để trống");
        }

        try {
            String uploadDir = "uploads/avatars/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = id.toString() + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
            file.transferTo(new File(dir.getAbsolutePath() + File.separator + filename));

            student.setAvatarUrl("/" + uploadDir + filename);
            studentRepository.save(student);

            return ApiResponse.success("Tải ảnh đại diện thành công", student.getAvatarUrl());
        } catch (IOException e) {
            log.error("Lỗi khi lưu file avatar: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Không thể lưu tệp tin ảnh");
        }
    }
}