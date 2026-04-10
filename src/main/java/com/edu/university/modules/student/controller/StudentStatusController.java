package com.edu.university.modules.student.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.student.dto.request.StudentStatusRequestDTO;
import com.edu.university.modules.student.dto.response.StudentStatusResponseDTO;
import com.edu.university.modules.student.service.StudentStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/student-status")
@RequiredArgsConstructor
public class StudentStatusController {

    private final StudentStatusService statusService;

    @PostMapping
    public ResponseEntity<BaseResponse<StudentStatusResponseDTO>> createStatus(
            @Valid @RequestBody StudentStatusRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Thêm trạng thái thành công", statusService.createStatus(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<StudentStatusResponseDTO>>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(statusService.getAll(pageable)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<BaseResponse<PageResponse<StudentStatusResponseDTO>>> getByStudentId(
            @PathVariable UUID studentId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(statusService.getByStudentId(studentId, pageable)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<StudentStatusResponseDTO>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StudentStatusRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật trạng thái thành công", statusService.updateStatus(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteStatus(@PathVariable UUID id) {
        statusService.deleteStatus(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa trạng thái thành công", null));
    }
}