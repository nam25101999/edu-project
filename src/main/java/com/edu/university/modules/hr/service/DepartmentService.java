package com.edu.university.modules.hr.service;

import com.edu.university.modules.hr.dto.request.DepartmentRequestDTO;
import com.edu.university.modules.hr.dto.response.DepartmentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequestDTO requestDTO);
    List<DepartmentResponseDTO> getAllDepartments();
    DepartmentResponseDTO getDepartmentById(UUID id);
    DepartmentResponseDTO getDepartmentByCode(String code);
    DepartmentResponseDTO updateDepartment(UUID id, DepartmentRequestDTO requestDTO);
    void deleteDepartment(UUID id);
}
