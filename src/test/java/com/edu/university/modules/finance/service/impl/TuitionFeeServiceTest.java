package com.edu.university.modules.finance.service.impl;

import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.dto.response.TuitionFeeResponseDTO;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.mapper.TuitionFeeMapper;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TuitionFeeServiceTest {

    @Mock
    private TuitionFeeRepository tuitionFeeRepository;
    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private TuitionFeeMapper tuitionFeeMapper;

    @InjectMocks
    private TuitionFeeServiceImpl tuitionFeeService;

    private TuitionFee tuitionFee;
    private TuitionFeeRequestDTO requestDTO;
    private TuitionFeeResponseDTO responseDTO;
    private UUID feeId;
    private UUID programId;

    @BeforeEach
    void setUp() {
        feeId = UUID.randomUUID();
        programId = UUID.randomUUID();
        
        tuitionFee = new TuitionFee();
        tuitionFee.setId(feeId);

        requestDTO = new TuitionFeeRequestDTO();
        requestDTO.setTrainingProgramId(programId);
        requestDTO.setBaseTuition(BigDecimal.valueOf(10000000));

        responseDTO = TuitionFeeResponseDTO.builder()
                .id(feeId)
                .trainingProgramId(programId)
                .baseTuition(BigDecimal.valueOf(10000000))
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(tuitionFeeMapper.toEntity(any())).thenReturn(tuitionFee);
        when(trainingProgramRepository.findById(programId)).thenReturn(Optional.of(new TrainingProgram()));
        when(tuitionFeeRepository.save(any())).thenReturn(tuitionFee);
        when(tuitionFeeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        TuitionFeeResponseDTO result = tuitionFeeService.create(requestDTO);

        // Assert
        assertNotNull(result);
        verify(tuitionFeeRepository).save(any());
    }

    @Test
    void create_ProgramNotFound() {
        // Arrange
        when(tuitionFeeMapper.toEntity(any())).thenReturn(tuitionFee);
        when(trainingProgramRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> tuitionFeeService.create(requestDTO));
    }

    @Test
    void getAll_Success() {
        // Arrange
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<TuitionFee> page = new org.springframework.data.domain.PageImpl<>(Collections.singletonList(tuitionFee));
        
        when(tuitionFeeRepository.findAll(pageable)).thenReturn(page);
        when(tuitionFeeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        org.springframework.data.domain.Page<TuitionFeeResponseDTO> results = tuitionFeeService.getAll(pageable);

        // Assert
        assertEquals(1, results.getTotalElements());
    }

    @Test
    void getById_Success() {
        // Arrange
        when(tuitionFeeRepository.findById(feeId)).thenReturn(Optional.of(tuitionFee));
        when(tuitionFeeMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        TuitionFeeResponseDTO result = tuitionFeeService.getById(feeId);

        // Assert
        assertEquals(feeId, result.getId());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(tuitionFeeRepository.findById(feeId)).thenReturn(Optional.of(tuitionFee));

        // Act
        tuitionFeeService.delete(feeId);

        // Assert
        verify(tuitionFeeRepository).save(any());
    }
}
