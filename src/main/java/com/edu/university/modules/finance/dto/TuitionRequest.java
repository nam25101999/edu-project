package com.edu.university.modules.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TuitionRequest {

    @NotNull
    private UUID studentId;

    @NotBlank
    private String semester;

    @NotNull
    private Integer year;
}