package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.StudentCourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.StudentCourseSectionResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentCourseSectionService {
    StudentCourseSectionResponseDTO create(StudentCourseSectionRequestDTO requestDTO);
    Page<StudentCourseSectionResponseDTO> getAll(Pageable pageable);
    StudentCourseSectionResponseDTO getById(UUID id);
    void delete(UUID id);
}
