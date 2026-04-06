package com.edu.university.modules.curriculum.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.dto.request.CourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CourseResponseDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.mapper.CourseMapper;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
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
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private CourseRequestDTO requestDTO;
    private CourseResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(UUID.randomUUID());
        course.setCode("CS101");
        course.setName("Introduction to Computer Science");

        requestDTO = new CourseRequestDTO();
        requestDTO.setCode("CS101");
        requestDTO.setName("Introduction to Computer Science");
        requestDTO.setDepartmentId(UUID.randomUUID());

        responseDTO = CourseResponseDTO.builder()
                .id(course.getId())
                .code("CS101")
                .name("Introduction to Computer Science")
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(courseRepository.existsByCode(any())).thenReturn(false);
        when(departmentRepository.findById(any())).thenReturn(Optional.of(new Department()));
        when(courseMapper.toEntity(any())).thenReturn(course);
        when(courseRepository.save(any())).thenReturn(course);
        when(courseMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        CourseResponseDTO result = courseService.create(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("CS101", result.getCode());
        verify(courseRepository).save(any());
    }

    @Test
    void create_DuplicateCode() {
        // Arrange
        when(courseRepository.existsByCode(any())).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> courseService.create(requestDTO));
        assertEquals(ErrorCode.COURSE_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void create_DepartmentNotFound() {
        // Arrange
        when(courseRepository.existsByCode(any())).thenReturn(false);
        when(departmentRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> courseService.create(requestDTO));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAll_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> page = new PageImpl<>(Collections.singletonList(course));
        when(courseRepository.findAll(pageable)).thenReturn(page);
        when(courseMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<CourseResponseDTO> result = courseService.getAll(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(courseRepository).findAll(pageable);
    }

    @Test
    void getById_Success() {
        // Arrange
        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(courseMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        CourseResponseDTO result = courseService.getById(course.getId());

        // Assert
        assertEquals(course.getId(), result.getId());
    }

    @Test
    void getById_NotFound() {
        // Arrange
        when(courseRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> courseService.getById(course.getId()));
        assertEquals(ErrorCode.COURSE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void update_Success() {
        // Arrange
        when(courseRepository.findById(any())).thenReturn(Optional.of(course));
        when(departmentRepository.findById(any())).thenReturn(Optional.of(new Department()));
        when(courseRepository.save(any())).thenReturn(course);
        when(courseMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        CourseResponseDTO result = courseService.update(course.getId(), requestDTO);

        // Assert
        verify(courseMapper).updateEntityFromDTO(any(), any());
        verify(courseRepository).save(any());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(courseRepository.findById(any())).thenReturn(Optional.of(course));

        // Act
        courseService.delete(course.getId());

        // Assert
        verify(courseRepository).save(any());
    }
}
