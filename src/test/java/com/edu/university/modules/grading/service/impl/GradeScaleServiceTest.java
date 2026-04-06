package com.edu.university.modules.grading.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.grading.dto.request.GradeScaleRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeScaleResponseDTO;
import com.edu.university.modules.grading.entity.GradeScale;
import com.edu.university.modules.grading.mapper.GradeScaleMapper;
import com.edu.university.modules.grading.repository.GradeScaleRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GradeScaleServiceTest {

    @Mock
    private GradeScaleRepository gradeScaleRepository;
    @Mock
    private GradeScaleMapper gradeScaleMapper;

    @InjectMocks
    private GradeScaleServiceImpl gradeScaleService;

    private GradeScale gradeScale;
    private GradeScaleRequestDTO requestDTO;
    private GradeScaleResponseDTO responseDTO;
    private UUID scaleId;

    @BeforeEach
    void setUp() {
        scaleId = UUID.randomUUID();
        gradeScale = new GradeScale();
        gradeScale.setId(scaleId);
        gradeScale.setScaleCode("A");

        requestDTO = new GradeScaleRequestDTO();
        requestDTO.setScaleCode("A");
        requestDTO.setMinScore(BigDecimal.valueOf(8.5));
        requestDTO.setMaxScore(BigDecimal.valueOf(10.0));
        requestDTO.setLetterGrade("A");
        requestDTO.setGpaValue(BigDecimal.valueOf(4.0));
        requestDTO.setPass(true);

        responseDTO = GradeScaleResponseDTO.builder()
                .id(scaleId)
                .scaleCode("A")
                .minScore(BigDecimal.valueOf(8.5))
                .maxScore(BigDecimal.valueOf(10.0))
                .letterGrade("A")
                .gpaValue(BigDecimal.valueOf(4.0))
                .pass(true)
                .active(true)
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(gradeScaleMapper.toEntity(any())).thenReturn(gradeScale);
        when(gradeScaleRepository.save(any())).thenReturn(gradeScale);
        when(gradeScaleMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        GradeScaleResponseDTO result = gradeScaleService.create(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("A", result.getScaleCode());
        verify(gradeScaleRepository).save(any());
    }

    @Test
    void getAll_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<GradeScale> page = new PageImpl<>(Collections.singletonList(gradeScale));
        when(gradeScaleRepository.findAll(pageable)).thenReturn(page);
        when(gradeScaleMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<GradeScaleResponseDTO> result = gradeScaleService.getAll(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(gradeScaleRepository).findAll(pageable);
    }

    @Test
    void getById_Success() {
        // Arrange
        when(gradeScaleRepository.findById(scaleId)).thenReturn(Optional.of(gradeScale));
        when(gradeScaleMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        GradeScaleResponseDTO result = gradeScaleService.getById(scaleId);

        // Assert
        assertEquals(scaleId, result.getId());
    }

    @Test
    void getById_NotFound() {
        // Arrange
        when(gradeScaleRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> gradeScaleService.getById(scaleId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void update_Success() {
        // Arrange
        when(gradeScaleRepository.findById(scaleId)).thenReturn(Optional.of(gradeScale));
        when(gradeScaleRepository.save(any())).thenReturn(gradeScale);
        when(gradeScaleMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        GradeScaleResponseDTO result = gradeScaleService.update(scaleId, requestDTO);

        // Assert
        verify(gradeScaleMapper).updateEntityFromDTO(any(), any());
        verify(gradeScaleRepository).save(any());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(gradeScaleRepository.findById(scaleId)).thenReturn(Optional.of(gradeScale));

        // Act
        gradeScaleService.delete(scaleId);

        // Assert
        verify(gradeScaleRepository).save(any());
    }
}
