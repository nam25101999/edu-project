package com.edu.university.modules.hr.service;

import com.edu.university.modules.hr.dto.request.EmployeeRequestDTO;
import com.edu.university.modules.hr.dto.request.EmployeeStatusChangeRequestDTO;
import com.edu.university.modules.hr.dto.response.EmployeeResponseDTO;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO);
    List<EmployeeResponseDTO> getAllEmployees();
    EmployeeResponseDTO getEmployeeById(UUID id);
    EmployeeResponseDTO getEmployeeByCode(String code);
    EmployeeResponseDTO updateEmployee(UUID id, EmployeeRequestDTO requestDTO);
    void deleteEmployee(UUID id);
    EmployeeResponseDTO changeStatus(UUID id, EmployeeStatusChangeRequestDTO requestDTO);
}
