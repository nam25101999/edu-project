package com.edu.university.modules.elearning.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class SubmissionRequest {
    private UUID assignmentId;
    private UUID studentId;
    private String content;
    private String fileUrl;
}
