package com.edu.university.modules.examination.service.impl;

import com.edu.university.modules.examination.dto.request.ExamTypeRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamTypeResponseDTO;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.mapper.ExamTypeMapper;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamTypeServiceTest {

    @Mock
    private ExamTypeRepository examTypeRepository;
    @Mock
    private ExamTypeMapper examTypeMapper;

    @InjectMocks
    private ExamTypeServiceImpl examTypeService;

    private ExamType examType;
    private ExamTypeRequestDTO requestDTO;
    private ExamTypeResponseDTO responseDTO;
    private UUID examTypeId;

    @BeforeEach
    void setUp() {
        examTypeId = UUID.randomUUID();
        examType = new ExamType();
        examType.setId(examTypeId);
        examType.setName("Midterm");

        requestDTO = new ExamTypeRequestDTO();
        requestDTO.setName("Midterm");

        responseDTO = ExamTypeResponseDTO.builder()
                .id(examTypeId)
                .name("Midterm")
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(examTypeMapper.toEntity(any())).thenReturn(examType);
        when(examTypeRepository.save(any())).thenReturn(examType);
        when(examTypeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        ExamTypeResponseDTO result = examTypeService.create(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Midterm", result.getName());
        verify(examTypeRepository).save(any());
    }

    @Test
    void getAll_Success() {
        // Arrange
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<ExamType> page = new org.springframework.data.domain.PageImpl<>(Collections.singletonList(examType));
        
        when(examTypeRepository.findAll(pageable)).thenReturn(page);
        when(examTypeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        org.springframework.data.domain.Page<ExamTypeResponseDTO> result = examTypeService.getAll(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(examTypeRepository).findAll(pageable);
    }

    @Test
    void getById_Success() {
        // Arrange
        when(examTypeRepository.findById(examTypeId)).thenReturn(Optional.of(examType));
        when(examTypeMapper.toResponseDTO(examType)).thenReturn(responseDTO);

        // Act
        ExamTypeResponseDTO result = examTypeService.getById(examTypeId);

        // Assert
        assertEquals(examTypeId, result.getId());
    }

    @Test
    void getById_NotFound() {
        // Arrange
        when(examTypeRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> examTypeService.getById(examTypeId));
    }

    @Test
    void update_Success() {
        // Arrange
        when(examTypeRepository.findById(examTypeId)).thenReturn(Optional.of(examType));
        when(examTypeRepository.save(any())).thenReturn(examType);
        when(examTypeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        ExamTypeResponseDTO result = examTypeService.update(examTypeId, requestDTO);

        // Assert
        verify(examTypeMapper).updateEntityFromDTO(any(), any());
        verify(examTypeRepository).save(any());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(examTypeRepository.findById(examTypeId)).thenReturn(Optional.of(examType));

        // Act
        examTypeService.delete(examTypeId);

        // Assert
        verify(examTypeRepository).save(examType);
    }
}
