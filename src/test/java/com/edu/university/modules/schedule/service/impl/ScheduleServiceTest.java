package com.edu.university.modules.schedule.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.schedule.dto.request.ScheduleRequestDTO;
import com.edu.university.modules.schedule.dto.response.ScheduleResponseDTO;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.mapper.ScheduleMapper;
import com.edu.university.modules.schedule.repository.RoomRepository;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
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
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private CourseSectionRepository courseSectionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ScheduleMapper scheduleMapper;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    private Schedule schedule;
    private ScheduleRequestDTO requestDTO;
    private ScheduleResponseDTO responseDTO;
    private UUID scheduleId;
    private UUID sectionId;
    private UUID lecturerId;
    private UUID roomId;

    @BeforeEach
    void setUp() {
        scheduleId = UUID.randomUUID();
        sectionId = UUID.randomUUID();
        lecturerId = UUID.randomUUID();
        roomId = UUID.randomUUID();

        schedule = new Schedule();
        schedule.setId(scheduleId);

        requestDTO = new ScheduleRequestDTO();
        requestDTO.setCourseSectionId(sectionId);
        requestDTO.setLecturerId(lecturerId);
        requestDTO.setRoomId(roomId);

        responseDTO = ScheduleResponseDTO.builder()
                .id(scheduleId)
                .courseSectionId(sectionId)
                .lecturerId(lecturerId)
                .roomId(roomId)
                .build();
    }

    @Test
    void create_Success() {
        // Arrange
        when(scheduleMapper.toEntity(any())).thenReturn(schedule);
        when(courseSectionRepository.findById(sectionId)).thenReturn(Optional.of(new CourseSection()));
        when(userRepository.findById(lecturerId)).thenReturn(Optional.of(new Users()));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(new Room()));
        when(scheduleRepository.save(any())).thenReturn(schedule);
        when(scheduleMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        ScheduleResponseDTO result = scheduleService.create(requestDTO);

        // Assert
        assertNotNull(result);
        verify(scheduleRepository).save(any());
    }

    @Test
    void create_CourseSectionNotFound() {
        // Arrange
        when(scheduleMapper.toEntity(any())).thenReturn(schedule);
        when(courseSectionRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> scheduleService.create(requestDTO));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAll_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Schedule> page = new PageImpl<>(Collections.singletonList(schedule));
        when(scheduleRepository.findAll(pageable)).thenReturn(page);
        when(scheduleMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<ScheduleResponseDTO> result = scheduleService.getAll(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getById_Success() {
        // Arrange
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(scheduleMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        ScheduleResponseDTO result = scheduleService.getById(scheduleId);

        // Assert
        assertEquals(scheduleId, result.getId());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // Act
        scheduleService.delete(scheduleId);

        // Assert
        verify(scheduleRepository).save(any());
    }
}
