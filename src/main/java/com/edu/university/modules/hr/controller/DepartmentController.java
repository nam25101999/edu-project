package com.edu.university.modules.hr.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.hr.dto.request.DepartmentRequestDTO;
import com.edu.university.modules.hr.dto.response.DepartmentResponseDTO;
import com.edu.university.modules.hr.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<BaseResponse<DepartmentResponseDTO>> createDepartment(@Valid @RequestBody DepartmentRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo phòng ban thành công", departmentService.createDepartment(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<DepartmentResponseDTO>>> getAllDepartments(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(departmentService.getAllDepartments(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<DepartmentResponseDTO>> getDepartmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(departmentService.getDepartmentById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<BaseResponse<DepartmentResponseDTO>> getDepartmentByCode(@PathVariable String code) {
        return ResponseEntity.ok(BaseResponse.ok(departmentService.getDepartmentByCode(code)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<DepartmentResponseDTO>> updateDepartment(
            @PathVariable UUID id,
            @Valid @RequestBody DepartmentRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật phòng ban thành công", departmentService.updateDepartment(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa phòng ban thành công", null));
    }
}
