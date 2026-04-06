package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.StudentStatusRequestDTO;
import com.edu.university.modules.student.dto.response.StudentStatusResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentStatusService {
    StudentStatusResponseDTO createStatus(StudentStatusRequestDTO requestDTO);
    Page<StudentStatusResponseDTO> getAll(Pageable pageable);
    Page<StudentStatusResponseDTO> getByStudentId(UUID studentId, Pageable pageable);
    StudentStatusResponseDTO updateStatus(UUID id, StudentStatusRequestDTO requestDTO);
    void deleteStatus(UUID id);
}