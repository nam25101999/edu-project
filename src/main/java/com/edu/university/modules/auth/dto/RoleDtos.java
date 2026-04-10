package com.edu.university.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

public class RoleDtos {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoleRequestDTO {
        @NotBlank(message = "Tên vai trò không được để trống")
        private String name;
        
        private String description;
        
        private Set<Long> permissionIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoleResponseDTO {
        private Long id;
        private String name;
        private String description;
        private Set<PermissionDTO> permissions;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PermissionDTO {
        private Long id;
        private String name;
        private String resource;
        private String action;
        private String description;
    }
}
