package com.edu.university.modules.auth.controller;

import com.edu.university.common.response.BaseResponse;
import com.edu.university.modules.auth.dto.RoleDtos.PermissionDTO;
import com.edu.university.modules.auth.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionRepository permissionRepository;

    @GetMapping
    public ResponseEntity<BaseResponse<List<PermissionDTO>>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionRepository.findAll().stream()
                .map(p -> PermissionDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .resource(p.getResource())
                        .action(p.getAction())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.ok(permissions));
    }
}
