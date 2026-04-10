package com.edu.university.modules.finance.service;

import com.edu.university.modules.finance.dto.request.StudentTuitionRequestDTO;
import com.edu.university.modules.finance.dto.response.StudentTuitionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentTuitionService {
    StudentTuitionResponseDTO create(StudentTuitionRequestDTO requestDTO);
    Page<StudentTuitionResponseDTO> getByStudentId(UUID studentId, Pageable pageable);
    Page<StudentTuitionResponseDTO> getBySemesterId(UUID semesterId, Pageable pageable);
    StudentTuitionResponseDTO update(UUID id, StudentTuitionRequestDTO requestDTO);
    void delete(UUID id);
}
