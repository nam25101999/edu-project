package com.edu.university.modules.grading.service;

import com.edu.university.modules.grading.dto.request.StudentComponentGradeRequestDTO;
import com.edu.university.modules.grading.dto.response.StudentComponentGradeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentComponentGradeService {
    StudentComponentGradeResponseDTO upsert(StudentComponentGradeRequestDTO requestDTO);
    Page<StudentComponentGradeResponseDTO> getByRegistrationId(UUID registrationId, Pageable pageable);
    void delete(UUID id);
}
