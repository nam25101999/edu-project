package com.edu.university.modules.studentservice.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class SurveyResponseRequest {
    private UUID studentId;
    private String answersJson;
}
