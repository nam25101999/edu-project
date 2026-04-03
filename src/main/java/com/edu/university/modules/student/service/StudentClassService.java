package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassResponseDTO;

import java.util.List;
import java.util.UUID;

public interface StudentClassService {
    StudentClassResponseDTO createClass(StudentClassRequestDTO requestDTO);
    List<StudentClassResponseDTO> getAllClasses();
    StudentClassResponseDTO getClassById(UUID id);
    StudentClassResponseDTO updateClass(UUID id, StudentClassRequestDTO requestDTO);
    void deleteClass(UUID id);
    List<StudentClassResponseDTO> getClassesByDepartmentAndMajor(UUID departmentId, UUID majorId);
}