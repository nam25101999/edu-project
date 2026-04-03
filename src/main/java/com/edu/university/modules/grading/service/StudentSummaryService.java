package com.edu.university.modules.grading.service;

import com.edu.university.modules.grading.dto.request.StudentSummaryRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentSummaryResponseDTO;

import java.util.UUID;

public interface StudentSummaryService {
    StudentSummaryResponseDTO upsert(StudentSummaryRequestDTO requestDTO);
    StudentSummaryResponseDTO getByRegistrationId(UUID registrationId);
    void delete(UUID id);
}
