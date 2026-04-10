package com.edu.university.modules.studentservice.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.dto.response.StudentPetitionResponseDTO;
import com.edu.university.modules.studentservice.entity.StudentPetition;
import com.edu.university.modules.studentservice.mapper.StudentPetitionMapper;
import com.edu.university.modules.studentservice.repository.StudentPetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentPetitionService {

    private final StudentPetitionRepository petitionRepository;
    private final StudentRepository studentRepository;
    private final StudentPetitionMapper petitionMapper;

    @Transactional
    public StudentPetitionResponseDTO createPetition(UUID studentId, String title, String content, String attachmentUrl) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        StudentPetition petition = StudentPetition.builder()
                .student(student)
                .title(title)
                .content(content)
                .status("PENDING")
                .attachmentUrl(attachmentUrl)
                .createdAt(LocalDateTime.now())
                .build();

        return petitionMapper.toResponseDTO(petitionRepository.save(petition));
    }

    @Transactional
    public StudentPetitionResponseDTO processPetition(UUID id, String status, String responseContent) {
        StudentPetition petition = petitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));
        
        petition.setStatus(status);
        petition.setResponseContent(responseContent);
        
        return petitionMapper.toResponseDTO(petitionRepository.save(petition));
    }

    @Transactional(readOnly = true)
    public Page<StudentPetitionResponseDTO> getPetitionsByStudent(UUID studentId, Pageable pageable) {
        return petitionRepository.findByStudentId(studentId, pageable)
                .map(petitionMapper::toResponseDTO);
    }
}
