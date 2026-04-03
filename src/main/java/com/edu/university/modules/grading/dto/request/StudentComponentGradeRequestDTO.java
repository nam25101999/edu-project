package com.edu.university.modules.grading.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class StudentComponentGradeRequestDTO {
    @NotNull(message = "ID đăng ký học phần không được để trống")
    private UUID registrationId;

    @NotNull(message = "ID thành phần điểm không được để trống")
    private UUID componentId;

    private BigDecimal score;
    private boolean isRetake;
    private boolean isLocked;
    private LocalDateTime gradedAt;
    private UUID gradedById;
    private String note;
}
