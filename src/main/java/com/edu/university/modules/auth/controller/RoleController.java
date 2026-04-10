package com.edu.university.modules.auth.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.auth.dto.RoleDtos.RoleRequestDTO;
import com.edu.university.modules.auth.dto.RoleDtos.RoleResponseDTO;
import com.edu.university.modules.auth.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<RoleResponseDTO>>> getAllRoles() {
        return ResponseEntity.ok(BaseResponse.ok(roleService.getAllRoles()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RoleResponseDTO>> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.ok(roleService.getRoleById(id)));
    }

    @PostMapping
    public ResponseEntity<BaseResponse<RoleResponseDTO>> createRole(@Valid @RequestBody RoleRequestDTO requestDTO) {
        return new ResponseEntity<>(
                BaseResponse.created("Tạo vai trò thành công", roleService.createRole(requestDTO)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<RoleResponseDTO>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO requestDTO) {
        return ResponseEntity.ok(BaseResponse.ok("Cập nhật vai trò thành công", roleService.updateRole(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(BaseResponse.ok("Xóa vai trò thành công", null));
    }
}
