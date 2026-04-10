package com.edu.university.modules.student.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.student.dto.request.StudentRequestDTO;
import com.edu.university.modules.student.dto.response.StudentResponseDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.mapper.StudentMapper;
import com.edu.university.modules.student.repository.StudentRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private CourseRegistrationRepository courseRegistrationRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentRequestDTO requestDTO;
    private StudentResponseDTO responseDTO;
    private UUID studentId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        student = new Student();
        student.setId(studentId);
        student.setStudentCode("SV001");

        requestDTO = new StudentRequestDTO();
        requestDTO.setStudentCode("SV001");
        requestDTO.setUserId(userId);
        requestDTO.setEmail("student@test.com");

        responseDTO = StudentResponseDTO.builder()
                .id(studentId)
                .studentCode("SV001")
                .userId(userId)
                .build();
    }

    @Test
    void createStudent_Success() {
        // Arrange
        when(studentRepository.existsByStudentCode(any())).thenReturn(false);
        when(studentMapper.toEntity(any())).thenReturn(student);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new Users()));
        when(studentRepository.save(any())).thenReturn(student);
        when(studentMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        StudentResponseDTO result = studentService.createStudent(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("SV001", result.getStudentCode());
        verify(studentRepository).save(any());
    }

    @Test
    void createStudent_DuplicateCode() {
        // Arrange
        when(studentRepository.existsByStudentCode(any())).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> studentService.createStudent(requestDTO));
        assertEquals(ErrorCode.ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void getAllStudents_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));
        // repository searchStudents(search, isActive, departmentId, majorId, studentClassId, pageable)
        when(studentRepository.searchStudents(null, null, null, null, null, pageable)).thenReturn(page);
        when(studentMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        // service getAllStudents(search, isActive, departmentId, majorId, studentClassId, courseSectionId, pageable)
        Page<StudentResponseDTO> result = studentService.getAllStudents(null, null, null, null, null, null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getStudentById_Success() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        StudentResponseDTO result = studentService.getStudentById(studentId);

        // Assert
        assertEquals(studentId, result.getId());
    }

    @Test
    void deleteStudent_Success() {
        // Arrange
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        // Act
        studentService.deleteStudent(studentId);

        // Assert
        verify(studentRepository).save(any());
    }
}
