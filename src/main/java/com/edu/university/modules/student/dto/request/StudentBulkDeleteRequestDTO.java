package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class StudentBulkDeleteRequestDTO {
    @NotEmpty(message = "Danh sách sinh viên không được để trống")
    private List<UUID> studentIds;
}
