package com.edu.university.modules.studentservice.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.entity.StudentPetition;
import com.edu.university.modules.studentservice.repository.StudentPetitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentPetitionService {

    private final StudentPetitionRepository petitionRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public StudentPetition createPetition(UUID studentId, String title, String content, String attachmentUrl) {
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

        return petitionRepository.save(petition);
    }

    @Transactional
    public StudentPetition processPetition(UUID id, String status, String responseContent) {
        StudentPetition petition = petitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));
        
        petition.setStatus(status);
        petition.setResponseContent(responseContent);
        
        return petitionRepository.save(petition);
    }

    public List<StudentPetition> getPetitionsByStudent(UUID studentId) {
        return petitionRepository.findByStudentId(studentId);
    }
}
