package com.edu.university.modules.student.service;

import com.edu.university.modules.student.dto.request.StudentStatusRequestDTO;
import com.edu.university.modules.student.dto.response.StudentStatusResponseDTO;

import java.util.List;
import java.util.UUID;

public interface StudentStatusService {
    StudentStatusResponseDTO createStatus(StudentStatusRequestDTO requestDTO);
    List<StudentStatusResponseDTO> getAll();
    List<StudentStatusResponseDTO> getByStudentId(UUID studentId);
    StudentStatusResponseDTO updateStatus(UUID id, StudentStatusRequestDTO requestDTO);
    void deleteStatus(UUID id);
}