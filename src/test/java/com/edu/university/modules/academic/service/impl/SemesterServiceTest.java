package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.dto.response.SemesterResponseDTO;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.mapper.SemesterMapper;
import com.edu.university.modules.academic.repository.SemesterRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SemesterServiceTest {

    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private SemesterMapper semesterMapper;

    @InjectMocks
    private SemesterServiceImpl semesterService;

    private Semester semester;
    private SemesterRequestDTO requestDTO;
    private SemesterResponseDTO responseDTO;
    private UUID semesterId;

    @BeforeEach
    void setUp() {
        semesterId = UUID.randomUUID();
        semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterCode("2023.1");

        requestDTO = new SemesterRequestDTO();
        requestDTO.setSemesterCode("2023.1");

        responseDTO = SemesterResponseDTO.builder()
                .id(semesterId)
                .semesterCode("2023.1")
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(semesterRepository.existsBySemesterCode(any())).thenReturn(false);
        when(semesterMapper.toEntity(any())).thenReturn(semester);
        when(semesterRepository.save(any())).thenReturn(semester);
        when(semesterMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        SemesterResponseDTO result = semesterService.create(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("2023.1", result.getSemesterCode());
        verify(semesterRepository).save(any());
        verify(semesterMapper).toResponseDTO(any());
    }

    @Test
    void create_DuplicateCode() {
        // Arrange
        when(semesterRepository.existsBySemesterCode(any())).thenReturn(true);

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> semesterService.create(requestDTO));
        assertEquals(ErrorCode.SEMESTER_CODE_EXISTS, ex.getErrorCode());
    }

    @Test
    void getAll_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Semester> page = new PageImpl<>(Collections.singletonList(semester));
        when(semesterRepository.findByAcademicYear_StartDateGreaterThanEqual(any(), eq(pageable))).thenReturn(page);
        when(semesterMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<SemesterResponseDTO> result = semesterService.getAll(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(semesterRepository).findByAcademicYear_StartDateGreaterThanEqual(any(), eq(pageable));
    }

    @Test
    void getById_Success() {
        // Arrange
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));
        when(semesterMapper.toResponseDTO(semester)).thenReturn(responseDTO);

        // Act
        SemesterResponseDTO result = semesterService.getById(semesterId);

        // Assert
        assertEquals(semesterId, result.getId());
    }

    @Test
    void getById_NotFound() {
        // Arrange
        when(semesterRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> semesterService.getById(semesterId));
        assertEquals(ErrorCode.SEMESTER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void update_Success() {
        // Arrange
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));
        when(semesterRepository.save(any())).thenReturn(semester);
        when(semesterMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        SemesterResponseDTO result = semesterService.update(semesterId, requestDTO);

        // Assert
        verify(semesterMapper).updateEntityFromDTO(any(), any());
        verify(semesterRepository).save(any());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));

        // Act
        semesterService.delete(semesterId);

        // Assert
        verify(semesterRepository).save(semester);
        // Note: softDelete only marks active=false, so checking repo save is enough
    }
}
