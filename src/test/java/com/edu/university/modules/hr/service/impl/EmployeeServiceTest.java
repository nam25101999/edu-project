package com.edu.university.modules.hr.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.hr.dto.request.EmployeeRequestDTO;
import com.edu.university.modules.hr.dto.response.EmployeeResponseDTO;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.mapper.EmployeeMapper;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import com.edu.university.modules.hr.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;
    private EmployeeResponseDTO responseDTO;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        employee = new Employee();
        employee.setId(employeeId);
        employee.setEmployeeCode("EMP001");

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setEmployeeCode("EMP001");
        requestDTO.setFullName("John Doe");

        responseDTO = EmployeeResponseDTO.builder()
                .id(employeeId)
                .employeeCode("EMP001")
                .fullName("John Doe")
                .build();
    }

    @Test
    void createEmployee_Success() {
        // Arrange
        when(employeeRepository.existsByEmployeeCode(any())).thenReturn(false);
        when(employeeMapper.toEntity(any())).thenReturn(employee);
        when(employeeRepository.save(any())).thenReturn(employee);
        when(employeeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("EMP001", result.getEmployeeCode());
        verify(employeeRepository).save(any());
    }

    @Test
    void createEmployee_DuplicateCode() {
        // Arrange
        when(employeeRepository.existsByEmployeeCode(any())).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> employeeService.createEmployee(requestDTO));
        assertEquals(ErrorCode.ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void getAllEmployees_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(Collections.singletonList(employee));
        when(employeeRepository.findAll(pageable)).thenReturn(page);
        when(employeeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<EmployeeResponseDTO> result = employeeService.getAllEmployees(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getEmployeeById_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        EmployeeResponseDTO result = employeeService.getEmployeeById(employeeId);

        // Assert
        assertEquals(employeeId, result.getId());
    }

    @Test
    void getByCode_Success() {
        // Arrange
        when(employeeRepository.findByEmployeeCode("EMP001")).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        EmployeeResponseDTO result = employeeService.getEmployeeByCode("EMP001");

        // Assert
        assertEquals("EMP001", result.getEmployeeCode());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployee(employeeId);

        // Assert
        verify(employeeRepository).save(any());
    }
}
