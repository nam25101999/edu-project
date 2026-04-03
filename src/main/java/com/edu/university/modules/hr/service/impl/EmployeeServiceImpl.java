package com.edu.university.modules.hr.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.hr.dto.request.EmployeeRequestDTO;
import com.edu.university.modules.hr.dto.request.EmployeeStatusChangeRequestDTO;
import com.edu.university.modules.hr.dto.response.EmployeeResponseDTO;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.mapper.EmployeeMapper;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import com.edu.university.modules.hr.repository.PositionRepository;
import com.edu.university.modules.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO requestDTO) {
        if (employeeRepository.existsByEmployeeCode(requestDTO.getEmployeeCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã nhân viên đã tồn tại");
        }
        Employee employee = employeeMapper.toEntity(requestDTO);
        
        if (requestDTO.getUserId() != null) {
            employee.setUser(userRepository.findById(requestDTO.getUserId()).orElse(null));
        }
        if (requestDTO.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(requestDTO.getDepartmentId()).orElse(null));
        }
        if (requestDTO.getPositionId() != null) {
            employee.setPosition(positionRepository.findById(requestDTO.getPositionId()).orElse(null));
        }

        employee.setActive(true);
        employee.setCreatedAt(LocalDateTime.now());
        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(saved);
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy nhân viên"));
        return employeeMapper.toResponseDTO(employee);
    }

    @Override
    public EmployeeResponseDTO getEmployeeByCode(String code) {
        Employee employee = employeeRepository.findByEmployeeCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy nhân viên"));
        return employeeMapper.toResponseDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO updateEmployee(UUID id, EmployeeRequestDTO requestDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy nhân viên"));
        employeeMapper.updateEntityFromDTO(requestDTO, employee);
        
        if (requestDTO.getUserId() != null) {
            employee.setUser(userRepository.findById(requestDTO.getUserId()).orElse(null));
        } else {
            employee.setUser(null);
        }
        if (requestDTO.getDepartmentId() != null) {
            employee.setDepartment(departmentRepository.findById(requestDTO.getDepartmentId()).orElse(null));
        } else {
            employee.setDepartment(null);
        }
        if (requestDTO.getPositionId() != null) {
            employee.setPosition(positionRepository.findById(requestDTO.getPositionId()).orElse(null));
        } else {
            employee.setPosition(null);
        }

        employee.setUpdatedAt(LocalDateTime.now());
        return employeeMapper.toResponseDTO(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy nhân viên"));
        employee.setActive(false);
        employee.setDeletedAt(LocalDateTime.now());
        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public EmployeeResponseDTO changeStatus(UUID id, EmployeeStatusChangeRequestDTO requestDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy nhân viên"));
        employee.setActive(requestDTO.getIsActive());
        employee.setUpdatedAt(LocalDateTime.now());
        return employeeMapper.toResponseDTO(employeeRepository.save(employee));
    }
}
