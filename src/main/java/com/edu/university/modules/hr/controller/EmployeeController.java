package com.edu.university.modules.hr.controller;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.hr.dto.request.EmployeeRequestDTO;
import com.edu.university.modules.hr.dto.request.EmployeeStatusChangeRequestDTO;
import com.edu.university.modules.hr.dto.response.EmployeeResponseDTO;
import com.edu.university.modules.hr.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody EmployeeRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo nhân viên thành công", employeeService.createEmployee(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<EmployeeResponseDTO>>> getAllEmployees(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.okPage(employeeService.getAllEmployees(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(employeeService.getEmployeeById(id)));
    }

    @GetMapping("/code/{employeeCode}")
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> getEmployeeByCode(@PathVariable String employeeCode) {
        return ResponseEntity.ok(BaseResponse.ok(employeeService.getEmployeeByCode(employeeCode)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật nhân viên thành công", employeeService.updateEmployee(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteEmployee(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa nhân viên thành công", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<EmployeeResponseDTO>> changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeStatusChangeRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Thay đổi trạng thái nhân viên thành công", employeeService.changeStatus(id, requestDTO)));
    }
}
