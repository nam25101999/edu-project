package com.edu.university.modules.registration.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CourseRegistrationRequestDTO {
    @NotNull(message = "ID sinh viên không được để trống")
    private UUID studentId;

    @NotNull(message = "ID lớp học phần không được để trống")
    private UUID courseSectionId;

    @NotNull(message = "ID đợt đăng ký không được để trống")
    private UUID registrationPeriodId;

    private Integer registrationType;
    private UUID replacedGradeId;
    private LocalDateTime registeredAt;
    private Integer status;
    private boolean isPaid;
}
