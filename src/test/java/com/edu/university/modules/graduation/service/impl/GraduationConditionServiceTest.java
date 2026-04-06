package com.edu.university.modules.graduation.service.impl;

import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.graduation.dto.request.GraduationConditionRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationConditionResponseDTO;
import com.edu.university.modules.graduation.entity.GraduationCondition;
import com.edu.university.modules.graduation.mapper.GraduationConditionMapper;
import com.edu.university.modules.graduation.repository.GraduationConditionRepository;
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
class GraduationConditionServiceTest {

    @Mock
    private GraduationConditionRepository graduationConditionRepository;
    @Mock
    private TrainingProgramRepository trainingProgramRepository;
    @Mock
    private GraduationConditionMapper graduationConditionMapper;

    @InjectMocks
    private GraduationConditionServiceImpl graduationConditionService;

    private GraduationCondition condition;
    private GraduationConditionRequestDTO requestDTO;
    private GraduationConditionResponseDTO responseDTO;
    private UUID conditionId;
    private UUID programId;

    @BeforeEach
    void setUp() {
        conditionId = UUID.randomUUID();
        programId = UUID.randomUUID();
        
        condition = new GraduationCondition();
        condition.setId(conditionId);

        requestDTO = new GraduationConditionRequestDTO();
        requestDTO.setTrainingProgramId(programId);
        requestDTO.setAppliedCohort("K2020");

        responseDTO = GraduationConditionResponseDTO.builder()
                .id(conditionId)
                .trainingProgramId(programId)
                .appliedCohort("K2020")
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(graduationConditionMapper.toEntity(any())).thenReturn(condition);
        when(trainingProgramRepository.findById(programId)).thenReturn(Optional.of(new TrainingProgram()));
        when(graduationConditionRepository.save(any())).thenReturn(condition);
        when(graduationConditionMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        GraduationConditionResponseDTO result = graduationConditionService.create(requestDTO);

        // Assert
        assertNotNull(result);
        verify(graduationConditionRepository).save(any());
    }

    @Test
    void create_ProgramNotFound() {
        // Arrange
        when(graduationConditionMapper.toEntity(any())).thenReturn(condition);
        when(trainingProgramRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> graduationConditionService.create(requestDTO));
    }

    @Test
    void getAll_Success() {
        // Arrange
        when(graduationConditionRepository.findAll()).thenReturn(Collections.singletonList(condition));
        when(graduationConditionMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        List<GraduationConditionResponseDTO> results = graduationConditionService.getAll();

        // Assert
        assertEquals(1, results.size());
    }
}
