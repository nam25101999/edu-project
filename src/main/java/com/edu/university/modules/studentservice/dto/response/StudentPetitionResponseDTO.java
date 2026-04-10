package com.edu.university.modules.studentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentPetitionResponseDTO {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private String studentCode;
    private String title;
    private String content;
    private String status;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private String responseContent;
}
