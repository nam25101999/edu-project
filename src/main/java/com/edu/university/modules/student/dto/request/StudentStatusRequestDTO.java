package com.edu.university.modules.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentStatusRequestDTO {
    @NotNull(message = "ID sinh viên không được để trống")
    private UUID studentId;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status; // VD: "Bảo lưu", "Thôi học", "Tốt nghiệp"

    private String reason; // Lý do thay đổi trạng thái
    private String note;

    @NotNull(message = "Ngày hiệu lực không được để trống")
    private LocalDate effectiveDate;
}