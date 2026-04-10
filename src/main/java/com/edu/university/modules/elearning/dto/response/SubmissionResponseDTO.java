package com.edu.university.modules.elearning.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponseDTO {
    private UUID id;
    private UUID assignmentId;
    private String assignmentTitle;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private String content;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private Double score;
    private String feedback;
    private boolean isGraded;
    private LocalDateTime createdAt;
    private boolean isActive;
}
