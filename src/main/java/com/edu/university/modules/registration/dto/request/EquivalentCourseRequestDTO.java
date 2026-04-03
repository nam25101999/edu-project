package com.edu.university.modules.registration.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class EquivalentCourseRequestDTO {
    @NotNull(message = "ID môn gốc không được để trống")
    private UUID originalCourseId;

    @NotNull(message = "ID môn thay thế không được để trống")
    private UUID equivalentCourseId;

    private Integer equivalenceType;
    private LocalDate effectDate;
    private String note;
}
