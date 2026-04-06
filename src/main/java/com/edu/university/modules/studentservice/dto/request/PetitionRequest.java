package com.edu.university.modules.studentservice.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class PetitionRequest {
    private UUID studentId;
    private String title;
    private String content;
    private String attachmentUrl;
}
