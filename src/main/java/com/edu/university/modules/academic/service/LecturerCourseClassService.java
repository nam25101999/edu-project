package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.LecturerCourseClassRequestDTO;
import com.edu.university.modules.academic.dto.response.LecturerCourseClassResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LecturerCourseClassService {
    LecturerCourseClassResponseDTO create(LecturerCourseClassRequestDTO requestDTO);
    Page<LecturerCourseClassResponseDTO> getAll(Pageable pageable);
    LecturerCourseClassResponseDTO getById(UUID id);
    void delete(UUID id);
}
