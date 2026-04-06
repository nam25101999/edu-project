package com.edu.university.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String username;
    private String email;
    private List<String> roles;
    private boolean isActive;
    private boolean emailVerified;
    private String lastLoginAt;
}
