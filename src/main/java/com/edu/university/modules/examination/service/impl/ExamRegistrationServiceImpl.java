package com.edu.university.modules.examination.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy kỳ thi"));
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        
        if (requestDTO.getExamRoomId() != null) {
            ExamRoom room = examRoomRepository.findById(requestDTO.getExamRoomId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy phòng thi"));
            registration.setExamRoom(room);
        }
        
        registration.setExam(exam);
        registration.setStudent(student);
        registration.setActive(true);
        return examRegistrationMapper.toResponseDTO(examRegistrationRepository.save(registration));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamRegistrationResponseDTO> getByExamId(UUID examId, Pageable pageable) {
        return examRegistrationRepository.findByExamId(examId, pageable)
                .map(examRegistrationMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamRegistrationResponseDTO> getByStudentId(UUID studentId, Pageable pageable) {
        return examRegistrationRepository.findByStudentId(studentId, pageable)
                .map(examRegistrationMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ExamRegistration registration = examRegistrationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy đăng ký thi"));
        registration.softDelete("system");
        examRegistrationRepository.save(registration);
    }
}
