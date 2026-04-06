package com.edu.university.modules.elearning.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssignmentRequest {
    private UUID courseSectionId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Double maxScore;
    private String attachmentUrl;
}
