package com.edu.university.modules.finance.service;

import com.edu.university.modules.finance.dto.request.StudentTuitionRequestDTO;
import com.edu.university.modules.finance.dto.response.StudentTuitionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface StudentTuitionService {
    StudentTuitionResponseDTO create(StudentTuitionRequestDTO requestDTO);
    List<StudentTuitionResponseDTO> getByStudentId(UUID studentId);
    List<StudentTuitionResponseDTO> getBySemesterId(UUID semesterId);
    StudentTuitionResponseDTO update(UUID id, StudentTuitionRequestDTO requestDTO);
    void delete(UUID id);
}
