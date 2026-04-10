package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class StudentBulkStatusRequestDTO {
    @NotEmpty(message = "Danh sách sinh viên không được để trống")
    private List<UUID> studentIds;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive;
}
