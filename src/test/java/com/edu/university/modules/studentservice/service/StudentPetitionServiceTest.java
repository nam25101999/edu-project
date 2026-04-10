package com.edu.university.modules.studentservice.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.dto.response.StudentPetitionResponseDTO;
import com.edu.university.modules.studentservice.entity.StudentPetition;
import com.edu.university.modules.studentservice.mapper.StudentPetitionMapper;
import com.edu.university.modules.studentservice.repository.StudentPetitionRepository;
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
class StudentPetitionServiceTest {

    @Mock
    private StudentPetitionRepository petitionRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentPetitionMapper petitionMapper;

    @InjectMocks
    private StudentPetitionService studentPetitionService;

    private StudentPetition petition;
    private StudentPetitionResponseDTO responseDTO;
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

        responseDTO = new StudentPetitionResponseDTO();
        responseDTO.setId(petitionId);
        responseDTO.setStatus("PENDING");
    }

    @Test
    void createPetition_Success() {
        // Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(petitionRepository.save(any())).thenReturn(petition);
        when(petitionMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        StudentPetitionResponseDTO result = studentPetitionService.createPetition(studentId, "Title", "Content", "url");

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
        when(petitionMapper.toResponseDTO(any())).thenReturn(responseDTO);
        responseDTO.setStatus("APPROVED");

        // Act
        StudentPetitionResponseDTO result = studentPetitionService.processPetition(petitionId, "APPROVED", "OK");

        // Assert
        assertEquals("APPROVED", result.getStatus());
        verify(petitionRepository).save(any());
    }

    @Test
    void getByStudent_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<StudentPetition> page = new PageImpl<>(Collections.singletonList(petition));
        when(petitionRepository.findByStudentId(eq(studentId), any(Pageable.class))).thenReturn(page);
        when(petitionMapper.toResponseDTO(any())).thenReturn(responseDTO);

        // Act
        Page<StudentPetitionResponseDTO> results = studentPetitionService.getPetitionsByStudent(studentId, pageable);

        // Assert
        assertEquals(1, results.getContent().size());
    }
}
