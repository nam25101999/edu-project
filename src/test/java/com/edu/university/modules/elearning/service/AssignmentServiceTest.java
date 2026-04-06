package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.entity.Assignment;
import com.edu.university.modules.elearning.repository.AssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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

    @InjectMocks
    private AssignmentService assignmentService;

    private UUID courseSectionId;
    private CourseSection courseSection;
    private Assignment assignment;

    @BeforeEach
    void setUp() {
        courseSectionId = UUID.randomUUID();
        courseSection = new CourseSection();
        courseSection.setId(courseSectionId);

        assignment = Assignment.builder()
                .id(UUID.randomUUID())
                .title("Homework 1")
                .courseSection(courseSection)
                .build();
    }

    @Test
    void createAssignment_Success() {
        // Arrange
        when(courseSectionRepository.findById(courseSectionId)).thenReturn(Optional.of(courseSection));
        when(assignmentRepository.save(any())).thenReturn(assignment);

        // Act
        Assignment result = assignmentService.createAssignment(
                courseSectionId, "Title", "Desc", LocalDateTime.now(), 10.0, "url");

        // Assert
        assertNotNull(result);
        verify(assignmentRepository).save(any());
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
        when(assignmentRepository.findByCourseSectionId(courseSectionId)).thenReturn(Collections.singletonList(assignment));

        // Act
        List<Assignment> results = assignmentService.getAssignmentsByCourseSection(courseSectionId);

        // Assert
        assertEquals(1, results.size());
        verify(assignmentRepository).findByCourseSectionId(courseSectionId);
    }
}
