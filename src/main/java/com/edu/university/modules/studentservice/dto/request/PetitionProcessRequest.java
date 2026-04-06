package com.edu.university.modules.studentservice.dto.request;

import lombok.Data;

@Data
public class PetitionProcessRequest {
    private String status; // APPROVED, REJECTED
    private String responseContent;
}
