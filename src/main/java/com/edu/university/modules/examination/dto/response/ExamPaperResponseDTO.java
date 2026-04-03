package com.edu.university.modules.examination.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExamPaperResponseDTO {
    private UUID id;
    private UUID examId;
    private String paperCode;
    private String fileUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
