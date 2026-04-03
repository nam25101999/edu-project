package com.edu.university.modules.examination.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExamRegistrationResponseDTO {
    private UUID id;
    private UUID examId;
    private UUID examRoomId;
    private String examRoomName;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private String rollNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
