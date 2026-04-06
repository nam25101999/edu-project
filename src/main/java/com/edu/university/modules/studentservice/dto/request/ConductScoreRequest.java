package com.edu.university.modules.studentservice.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class ConductScoreRequest {
    private UUID studentId;
    private UUID semesterId;
    private Integer score;
}
