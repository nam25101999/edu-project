package com.edu.university.modules.academic.service;

import com.edu.university.modules.academic.dto.request.LecturerCourseClassRequestDTO;
import com.edu.university.modules.academic.dto.response.LecturerCourseClassResponseDTO;

import java.util.List;
import java.util.UUID;

public interface LecturerCourseClassService {
    LecturerCourseClassResponseDTO create(LecturerCourseClassRequestDTO requestDTO);
    List<LecturerCourseClassResponseDTO> getAll();
    LecturerCourseClassResponseDTO getById(UUID id);
    void delete(UUID id);
}
