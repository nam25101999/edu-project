package com.edu.university.modules.grading.service;

import com.edu.university.modules.grading.dto.request.StudentComponentGradeRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentComponentGradeResponseDTO;

import java.util.List;
import java.util.UUID;

public interface StudentComponentGradeService {
    StudentComponentGradeResponseDTO upsert(StudentComponentGradeRequestDTO requestDTO);
    List<StudentComponentGradeResponseDTO> getByRegistrationId(UUID registrationId);
    void delete(UUID id);
}
