package com.edu.university.modules.elearning.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialResponseDTO {
    private UUID id;
    private UUID courseSectionId;
    private String classCode;
    private String title;
    private String description;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private LocalDateTime createdAt;
    private boolean isActive;
}
