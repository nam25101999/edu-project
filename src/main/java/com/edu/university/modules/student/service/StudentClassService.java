package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.StudentClassRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentClassService {
    StudentClassResponseDTO createClass(StudentClassRequestDTO requestDTO);
    Page<StudentClassResponseDTO> getAllClasses(Pageable pageable);
    StudentClassResponseDTO getClassById(UUID id);
    StudentClassResponseDTO updateClass(UUID id, StudentClassRequestDTO requestDTO);
    void deleteClass(UUID id);
    Page<StudentClassResponseDTO> getClassesByDepartmentAndMajor(UUID departmentId, UUID majorId, Pageable pageable);
}