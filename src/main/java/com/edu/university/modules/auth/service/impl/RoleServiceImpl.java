package com.edu.university.modules.auth.service.impl;

import com.edu.university.common.exception.AppException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.dto.RoleDtos.*;
import com.edu.university.modules.auth.entity.Permission;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.repository.PermissionRepository;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        return mapToResponse(role);
    }

    @Override
    @Transactional
    public RoleResponseDTO createRole(RoleRequestDTO requestDTO) {
        if (roleRepository.findByName(requestDTO.getName()).isPresent()) {
            throw new AppException(ErrorCode.ALREADY_EXISTS);
        }

        Role role = Role.builder()
                .name(requestDTO.getName())
                .description(requestDTO.getDescription())
                .permissions(resolvePermissions(requestDTO.getPermissionIds()))
                .build();

        return mapToResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO requestDTO) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));

        role.setName(requestDTO.getName());
        role.setDescription(requestDTO.getDescription());
        role.setPermissions(resolvePermissions(requestDTO.getPermissionIds()));

        return mapToResponse(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new AppException(ErrorCode.DATA_NOT_FOUND);
        }
        roleRepository.deleteById(id);
    }

    private Set<Permission> resolvePermissions(Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(permissionRepository.findAllById(permissionIds));
    }

    private RoleResponseDTO mapToResponse(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(role.getPermissions().stream()
                        .map(p -> PermissionDTO.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .resource(p.getResource())
                                .action(p.getAction())
                                .description(p.getDescription())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}
