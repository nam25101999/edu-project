package com.edu.university.modules.auth.service;

import com.edu.university.modules.auth.dto.RoleDtos.*;

import java.util.List;

public interface RoleService {
    List<RoleResponseDTO> getAllRoles();
    RoleResponseDTO getRoleById(Long id);
    RoleResponseDTO createRole(RoleRequestDTO requestDTO);
    RoleResponseDTO updateRole(Long id, RoleRequestDTO requestDTO);
    void deleteRole(Long id);
}
