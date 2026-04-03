package com.edu.university.modules.examination.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ExamRegistrationRequestDTO {
    @NotNull(message = "ID kỳ thi không được để trống")
    private UUID examId;

    private UUID examRoomId;

    @NotNull(message = "ID sinh viên không được để trống")
    private UUID studentId;

    private String rollNumber;
}
