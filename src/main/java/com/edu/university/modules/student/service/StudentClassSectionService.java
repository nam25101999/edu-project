package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.StudentClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassSectionResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentClassSectionService {
    StudentClassSectionResponseDTO addStudentToClass(StudentClassSectionRequestDTO requestDTO);
    Page<StudentClassSectionResponseDTO> getAll(Pageable pageable);
    Page<StudentClassSectionResponseDTO> getByStudentId(UUID studentId, Pageable pageable);
    Page<StudentClassSectionResponseDTO> getByClassId(UUID studentClassesId, Pageable pageable);
    StudentClassSectionResponseDTO update(UUID id, StudentClassSectionRequestDTO requestDTO);
    void delete(UUID id);
}