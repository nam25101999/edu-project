package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.StudentCourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.StudentCourseSectionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface StudentCourseSectionService {
    StudentCourseSectionResponseDTO create(StudentCourseSectionRequestDTO requestDTO);
    List<StudentCourseSectionResponseDTO> getAll();
    StudentCourseSectionResponseDTO getById(UUID id);
    void delete(UUID id);
}
