package com.edu.university.modules.auth.mapper;

import com.edu.university.modules.auth.dto.UserResponseDTO;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AuthMapper {

    public UserResponseDTO toUserResponseDTO(Users user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .isActive(user.isActive())
                .emailVerified(user.isEmailVerified())
                .lastLoginAt(user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null)
                .build();
    }
}
