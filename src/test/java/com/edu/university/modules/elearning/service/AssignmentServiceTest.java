package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.dto.response.AssignmentResponseDTO;
import com.edu.university.modules.elearning.entity.Assignment;
import com.edu.university.modules.elearning.mapper.AssignmentMapper;
import com.edu.university.modules.elearning.repository.AssignmentRepository;
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
class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private CourseSectionRepository courseSectionRepository;
    @Mock
    private AssignmentMapper assignmentMapper;

    @InjectMocks
    private AssignmentService assignmentService;

    private UUID courseSectionId;
    private CourseSection courseSection;
    private Assignment assignment;
    private AssignmentResponseDTO assignmentResponseDTO;

    @BeforeEach
    void setUp() {
        courseSectionId = UUID.randomUUID();
        courseSection = new CourseSection();
        courseSection.setId(courseSectionId);
        courseSection.setClassCode("CS101");

        assignment = Assignment.builder()
                .id(UUID.randomUUID())
                .title("Homework 1")
                .courseSection(courseSection)
                .build();
        
        assignmentResponseDTO = AssignmentResponseDTO.builder()
                .id(assignment.getId())
                .title("Homework 1")
                .classCode("CS101")
                .build();
    }

    @Test
    void createAssignment_Success() {
        // Arrange
        when(courseSectionRepository.findById(courseSectionId)).thenReturn(Optional.of(courseSection));
        when(assignmentRepository.save(any())).thenReturn(assignment);
        when(assignmentMapper.toResponseDTO(any())).thenReturn(assignmentResponseDTO);

        // Act
        AssignmentResponseDTO result = assignmentService.createAssignment(
                courseSectionId, "Title", "Desc", LocalDateTime.now(), 10.0, "url");

        // Assert
        assertNotNull(result);
        assertEquals(assignmentResponseDTO.getTitle(), result.getTitle());
        verify(assignmentRepository).save(any());
        verify(assignmentMapper).toResponseDTO(any());
    }

    @Test
    void createAssignment_CourseSectionNotFound() {
        // Arrange
        when(courseSectionRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            assignmentService.createAssignment(courseSectionId, "Title", "Desc", LocalDateTime.now(), 10.0, "url")
        );
        assertEquals(ErrorCode.CLASS_SECTION_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAssignmentsByCourseSection_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Assignment> assignmentPage = new PageImpl<>(Collections.singletonList(assignment));
        when(assignmentRepository.findByCourseSectionId(eq(courseSectionId), any(Pageable.class))).thenReturn(assignmentPage);
        when(assignmentMapper.toResponseDTO(any())).thenReturn(assignmentResponseDTO);

        // Act
        Page<AssignmentResponseDTO> results = assignmentService.getAssignmentsByCourseSection(courseSectionId, pageable);

        // Assert
        assertEquals(1, results.getTotalElements());
        assertEquals(assignmentResponseDTO.getTitle(), results.getContent().get(0).getTitle());
        verify(assignmentRepository).findByCourseSectionId(eq(courseSectionId), any(Pageable.class));
    }
}
