package com.edu.university.modules.studentservice.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.entity.StudentPetition;
import com.edu.university.modules.studentservice.repository.StudentPetitionRepository;
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
class StudentPetitionServiceTest {

    @Mock
    private StudentPetitionRepository petitionRepository;
    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentPetitionService studentPetitionService;

    private StudentPetition petition;
    private Student student;
    private UUID studentId;
    private UUID petitionId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        petitionId = UUID.randomUUID();

        student = new Student();
        student.setId(studentId);

        petition = StudentPetition.builder()
                .id(petitionId)
                .title("Petition Title")
                .student(student)
                .status("PENDING")
                .build();
    }

    @Test
    void createPetition_Success() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(petitionRepository.save(any())).thenReturn(petition);

        // Act
        StudentPetition result = studentPetitionService.createPetition(studentId, "Title", "Content", "url");

        // Assert
        assertNotNull(result);
        verify(petitionRepository).save(any());
    }

    @Test
    void createPetition_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException ex = assertThrows(BusinessException.class, () -> studentPetitionService.createPetition(studentId, "Title", "Content", "url"));
        assertEquals(ErrorCode.STUDENT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void processPetition_Success() {
        // Arrange
        when(petitionRepository.findById(petitionId)).thenReturn(Optional.of(petition));
        when(petitionRepository.save(any())).thenReturn(petition);

        // Act
        StudentPetition result = studentPetitionService.processPetition(petitionId, "APPROVED", "OK");

        // Assert
        assertEquals("APPROVED", result.getStatus());
        verify(petitionRepository).save(any());
    }

    @Test
    void getByStudent_Success() {
        // Arrange
        when(petitionRepository.findByStudentId(studentId)).thenReturn(Collections.singletonList(petition));

        // Act
        List<StudentPetition> results = studentPetitionService.getPetitionsByStudent(studentId);

        // Assert
        assertEquals(1, results.size());
    }
}
