package com.edu.university.modules.examination.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExamResultRequestDTO {
    @NotNull(message = "ID đăng ký thi không được để trống")
    private UUID registrationId;

    private BigDecimal score;
    private String status;
    private UUID gradedById;
    private LocalDateTime gradedAt;
    private boolean isLocked;
    private String appealStatus;
}
