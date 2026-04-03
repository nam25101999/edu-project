package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.StudentClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassSectionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface StudentClassSectionService {
    StudentClassSectionResponseDTO addStudentToClass(StudentClassSectionRequestDTO requestDTO);
    List<StudentClassSectionResponseDTO> getAll();
    List<StudentClassSectionResponseDTO> getByStudentId(UUID studentId);
    List<StudentClassSectionResponseDTO> getByClassId(UUID studentClassesId);
    StudentClassSectionResponseDTO update(UUID id, StudentClassSectionRequestDTO requestDTO);
    void delete(UUID id);
}