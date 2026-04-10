package com.edu.university.modules.student.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.student.dto.request.StudentBulkDeleteRequestDTO;
import com.edu.university.modules.student.dto.request.StudentBulkStatusRequestDTO;
import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.request.StudentStatusChangeRequestDTO;
import com.edu.university.modules.student.dto.response.StudentImportResultDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.dto.response.StudentStatsResponseDTO;
import com.edu.university.modules.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentResponseDTO>> createStudent(@Valid @RequestBody StudentRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tao sinh vien thanh cong", studentService.createStudent(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<StudentResponseDTO>>> getAllStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID majorId,
            @RequestParam(required = false) UUID studentClassId,
            @RequestParam(required = false) UUID courseSectionId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(studentService.getAllStudents(
                search, isActive, departmentId, majorId, studentClassId, courseSectionId, pageable)));
    }

    @GetMapping("/stats")
    public ResponseEntity<BaseResponse<StudentStatsResponseDTO>> getStudentStats() {
        return ResponseEntity.ok(BaseResponse.ok(studentService.getStudentStats()));
    }

    @GetMapping("/student-classes/{studentClassId}")
    public ResponseEntity<BaseResponse<PageResponse<StudentResponseDTO>>> getStudentsByStudentClass(
            @PathVariable UUID studentClassId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(studentService.getStudentsByStudentClass(studentClassId, search, isActive, pageable)));
    }

    @GetMapping("/course-sections/{courseSectionId}")
    public ResponseEntity<BaseResponse<PageResponse<StudentResponseDTO>>> getStudentsByCourseSection(
            @PathVariable UUID courseSectionId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(studentService.getStudentsByCourseSection(courseSectionId, search, isActive, pageable)));
    }

    @PostMapping("/bulk/status")
    public ResponseEntity<BaseResponse<List<StudentResponseDTO>>> bulkChangeStatus(
            @Valid @RequestBody StudentBulkStatusRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cap nhat trang thai sinh vien thanh cong", studentService.bulkChangeStatus(requestDTO)));
    }

    @PostMapping("/bulk/delete")
    public ResponseEntity<BaseResponse<Void>> bulkDeleteStudents(
            @Valid @RequestBody StudentBulkDeleteRequestDTO requestDTO) {
        studentService.bulkDeleteStudents(requestDTO);
        return ResponseEntity.ok(BaseResponse.ok("Xoa danh sach sinh vien thanh cong", null));
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<StudentImportResultDTO>> importStudents(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(BaseResponse.ok("Import sinh vien thanh cong", studentService.importStudents(file)));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStudents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID majorId,
            @RequestParam(required = false) UUID studentClassId,
            @RequestParam(required = false) UUID courseSectionId,
            @RequestParam(required = false) List<UUID> studentIds,
            @RequestParam(required = false) List<String> columns,
            @PageableDefault(size = 2000) Pageable pageable) {
        byte[] csv = studentService.exportStudents(search, isActive, departmentId, majorId, studentClassId, courseSectionId, studentIds, columns, pageable);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> getImportTemplate(
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID majorId,
            @RequestParam(required = false) UUID programId,
            @RequestParam(required = false) String classCode) {
        byte[] csv = studentService.getImportTemplate(departmentId, majorId, programId, classCode);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_import_template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
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
        return ResponseEntity.ok(BaseResponse.ok("Cap nhat sinh vien thanh cong", studentService.updateStudent(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(BaseResponse.ok("Xoa sinh vien thanh cong", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<StudentResponseDTO>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StudentStatusChangeRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Thay doi trang thai sinh vien thanh cong", studentService.changeStatus(id, requestDTO)));
    }
}
