package com.edu.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

// Gộp các DTO record thành 1 file để tránh spam quá nhiều file phụ
public class PayloadDtos {

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record JwtResponse(String token, UUID id, String username, String role) {}

    public record SignupRequest(@NotBlank String username, @NotBlank String password, @NotBlank String email, @NotBlank String role) {}

    public record EnrollmentRequest(@NotNull UUID classSectionId) {}

    public record GradeRequest(@NotNull Double attendance, @NotNull Double midterm, @NotNull Double finalScore) {}
}