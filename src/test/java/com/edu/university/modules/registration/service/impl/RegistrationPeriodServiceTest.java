package com.edu.university.modules.registration.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.registration.dto.request.RegistrationPeriodRequestDTO;
import com.edu.university.modules.registration.dto.response.RegistrationPeriodResponseDTO;
import com.edu.university.modules.registration.entity.RegistrationPeriod;
import com.edu.university.modules.registration.mapper.RegistrationPeriodMapper;
import com.edu.university.modules.registration.repository.RegistrationPeriodRepository;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationPeriodServiceTest {

    @Mock
    private RegistrationPeriodRepository registrationPeriodRepository;
    @Mock
    private SemesterRepository semesterRepository;
    @Mock
    private RegistrationPeriodMapper registrationPeriodMapper;

    @InjectMocks
    private RegistrationPeriodServiceImpl registrationPeriodService;

    private RegistrationPeriod period;
    private RegistrationPeriodRequestDTO requestDTO;
    private RegistrationPeriodResponseDTO responseDTO;
    private UUID periodId;
    private UUID semesterId;

    @BeforeEach
    void setUp() {
        periodId = UUID.randomUUID();
        semesterId = UUID.randomUUID();

        period = new RegistrationPeriod();
        period.setId(periodId);
        period.setName("Học kỳ 1 2023-2024");

        requestDTO = new RegistrationPeriodRequestDTO();
        requestDTO.setName("Học kỳ 1 2023-2024");
        requestDTO.setSemesterId(semesterId);
        requestDTO.setStartTime(LocalDateTime.now().plusDays(1));
        requestDTO.setEndTime(LocalDateTime.now().plusDays(10));

        responseDTO = RegistrationPeriodResponseDTO.builder()
                .id(periodId)
                .name("Học kỳ 1 2023-2024")
                .semesterId(semesterId)
                .startTime(requestDTO.getStartTime())
                .endTime(requestDTO.getEndTime())
                .isActive(true)
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(registrationPeriodMapper.toEntity(any())).thenReturn(period);
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(new Semester()));
        when(registrationPeriodRepository.save(any())).thenReturn(period);
        when(registrationPeriodMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        RegistrationPeriodResponseDTO result = registrationPeriodService.create(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Học kỳ 1 2023-2024", result.getName());
        verify(registrationPeriodRepository).save(any());
    }

    @Test
    void create_SemesterNotFound() {
        // Arrange
        when(registrationPeriodMapper.toEntity(any())).thenReturn(period);
        when(semesterRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> registrationPeriodService.create(requestDTO));
        assertEquals(ErrorCode.SEMESTER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAll_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<RegistrationPeriod> page = new PageImpl<>(Collections.singletonList(period));
        when(registrationPeriodRepository.findAll(pageable)).thenReturn(page);
        when(registrationPeriodMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<RegistrationPeriodResponseDTO> result = registrationPeriodService.getAll(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void update_Success() {
        // Arrange
        when(registrationPeriodRepository.findById(periodId)).thenReturn(Optional.of(period));
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(new Semester()));
        when(registrationPeriodRepository.save(any())).thenReturn(period);
        when(registrationPeriodMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        RegistrationPeriodResponseDTO result = registrationPeriodService.update(periodId, requestDTO);

        // Assert
        assertNotNull(result);
        verify(registrationPeriodMapper).updateEntityFromDTO(any(), any());
        verify(registrationPeriodRepository).save(any());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(registrationPeriodRepository.findById(periodId)).thenReturn(Optional.of(period));

        // Act
        registrationPeriodService.delete(periodId);

        // Assert
        verify(registrationPeriodRepository).save(any());
    }
}
