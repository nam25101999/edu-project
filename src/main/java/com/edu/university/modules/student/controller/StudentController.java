package com.edu.university.modules.student.controller;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.common.response.ApiResponse;
import com.edu.university.modules.student.dto.StudentDtos.StudentRequest;
import com.edu.university.modules.student.dto.StudentDtos.StudentResponse;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;
    private final StudentRepository studentRepository;

    /**
     * Lấy danh sách sinh viên có lọc theo từ khóa và ngành học.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<StudentResponse>> getAllStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID majorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(studentService.searchAndFilterStudents(keyword, majorId, page, size));
    }

    /**
     * Lấy danh sách sinh viên theo một ngành cụ thể.
     */
    @GetMapping("/major/{majorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ApiResponse<Page<StudentResponse>> getStudentsByMajor(
            @PathVariable UUID majorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(studentService.getStudentsByMajor(majorId, page, size));
    }

    /**
     * Lấy danh sách sinh viên theo khoa.
     */
    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ApiResponse<Page<StudentResponse>> getStudentsByFaculty(
            @PathVariable UUID facultyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(studentService.getStudentsByFaculty(facultyId, page, size));
    }

    /**
     * Endpoint mới: Đếm số lượng sinh viên theo ngành.
     */
    @GetMapping("/count/major/{majorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ApiResponse<Long> countStudentsByMajor(@PathVariable UUID majorId) {
        return ApiResponse.success(studentService.countStudentsByMajor(majorId));
    }

    /**
     * Endpoint mới: Đếm số lượng sinh viên theo khoa.
     */
    @GetMapping("/count/faculty/{facultyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ApiResponse<Long> countStudentsByFaculty(@PathVariable UUID facultyId) {
        return ApiResponse.success(studentService.countStudentsByFaculty(facultyId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'LECTURER')")
    public ApiResponse<StudentResponse> getStudentById(@PathVariable UUID id) {
        return ApiResponse.success(studentService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        return ApiResponse.created("Tạo sinh viên thành công", studentService.createStudent(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StudentResponse> updateStudent(@PathVariable UUID id, @Valid @RequestBody StudentRequest request) {
        return ApiResponse.success("Cập nhật thông tin thành công", studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ApiResponse.success("Xóa sinh viên thành công", null);
    }

    @PostMapping("/{id}/avatar")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ApiResponse<String> uploadAvatar(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Không tìm thấy sinh viên"));

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