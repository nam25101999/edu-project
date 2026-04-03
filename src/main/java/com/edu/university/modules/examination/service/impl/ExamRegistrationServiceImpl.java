package com.edu.university.modules.examination.service.impl;

import com.edu.university.modules.examination.dto.request.ExamRegistrationRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamRegistrationResponseDTO;
import com.edu.university.modules.examination.entity.Exam;
import com.edu.university.modules.examination.entity.ExamRegistration;
import com.edu.university.modules.examination.entity.ExamRoom;
import com.edu.university.modules.examination.mapper.ExamRegistrationMapper;
import com.edu.university.modules.examination.repository.ExamRegistrationRepository;
import com.edu.university.modules.examination.repository.ExamRepository;
import com.edu.university.modules.examination.repository.ExamRoomRepository;
import com.edu.university.modules.examination.service.ExamRegistrationService;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamRegistrationServiceImpl implements ExamRegistrationService {

    private final ExamRegistrationRepository examRegistrationRepository;
    private final ExamRepository examRepository;
    private final ExamRoomRepository examRoomRepository;
    private final StudentRepository studentRepository;
    private final ExamRegistrationMapper examRegistrationMapper;

    @Override
    @Transactional
    public ExamRegistrationResponseDTO create(ExamRegistrationRequestDTO requestDTO) {
        ExamRegistration registration = examRegistrationMapper.toEntity(requestDTO);
        
        Exam exam = examRepository.findById(requestDTO.getExamId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỳ thi"));
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
        
        if (requestDTO.getExamRoomId() != null) {
            ExamRoom room = examRoomRepository.findById(requestDTO.getExamRoomId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng thi"));
            registration.setExamRoom(room);
        }
        
        registration.setExam(exam);
        registration.setStudent(student);
        registration.setActive(true);
        registration.setCreatedAt(LocalDateTime.now());
        return examRegistrationMapper.toResponseDTO(examRegistrationRepository.save(registration));
    }

    @Override
    public List<ExamRegistrationResponseDTO> getByExamId(UUID examId) {
        return examRegistrationRepository.findByExamId(examId).stream()
                .map(examRegistrationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamRegistrationResponseDTO> getByStudentId(UUID studentId) {
        return examRegistrationRepository.findByStudentId(studentId).stream()
                .map(examRegistrationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ExamRegistration registration = examRegistrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký thi"));
        registration.softDelete("system");
        examRegistrationRepository.save(registration);
    }
}
