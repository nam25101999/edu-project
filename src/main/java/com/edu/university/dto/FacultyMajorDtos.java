package com.edu.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class FacultyMajorDtos {

    public record FacultyRequest(
            @NotBlank(message = "Mã khoa không được để trống") String facultyCode,
            @NotBlank(message = "Tên khoa không được để trống") String name,
            String description,
            String contactEmail
    ) {}

    public record MajorRequest(
            @NotBlank(message = "Mã ngành không được để trống") String majorCode,
            @NotBlank(message = "Tên ngành không được để trống") String name,
            @NotNull(message = "ID của Khoa không được để trống") UUID facultyId
    ) {}
}