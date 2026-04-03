package com.edu.university.modules.examination.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ExamPaperRequestDTO {
    @NotNull(message = "ID kỳ thi không được để trống")
    private UUID examId;

    @NotBlank(message = "Mã đề thi không được để trống")
    private String paperCode;

    private String fileUrl;
}
