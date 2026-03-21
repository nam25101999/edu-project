package com.edu.university.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

// Gom nhóm các DTO liên quan đến Xác thực (Auth)
public class AuthDtos {

    public record LoginRequest(
            @NotBlank(message = "Username không được để trống") String username,
            @NotBlank(message = "Password không được để trống") String password
    ) {}

    public record JwtResponse(
            String token,
            UUID id,
            String username,
            String role
    ) {}

    public record SignupRequest(
            @NotBlank(message = "Username không được để trống") String username,
            @NotBlank(message = "Password không được để trống") String password,
            @NotBlank(message = "Email không được để trống") String email,
            @NotBlank(message = "Role không được để trống") String role
    ) {}
}