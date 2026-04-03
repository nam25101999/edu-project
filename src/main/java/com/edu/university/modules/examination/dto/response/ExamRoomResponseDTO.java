package com.edu.university.modules.examination.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExamRoomResponseDTO {
    private UUID id;
    private UUID examId;
    private UUID roomId;
    private String roomName;
    private Integer capacity;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
