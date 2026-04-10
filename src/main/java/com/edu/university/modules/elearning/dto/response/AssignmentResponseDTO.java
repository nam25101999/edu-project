package com.edu.university.modules.elearning.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentResponseDTO {
    private UUID id;
    private UUID courseSectionId;
    private String classCode;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Double maxScore;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private boolean isActive;
}
