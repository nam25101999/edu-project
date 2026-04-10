package com.edu.university.modules.finance.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.finance.dto.request.StudentTuitionRequestDTO;
import com.edu.university.modules.finance.dto.response.StudentTuitionResponseDTO;
import com.edu.university.modules.finance.entity.StudentTuition;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.mapper.StudentTuitionMapper;
import com.edu.university.modules.finance.repository.StudentTuitionRepository;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import com.edu.university.modules.finance.service.StudentTuitionService;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentTuitionServiceImpl implements StudentTuitionService {

    private final StudentTuitionRepository studentTuitionRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final TuitionFeeRepository tuitionFeeRepository;
    private final StudentTuitionMapper studentTuitionMapper;

    @Override
    @Transactional
    public StudentTuitionResponseDTO create(StudentTuitionRequestDTO requestDTO) {
        StudentTuition tuition = studentTuitionMapper.toEntity(requestDTO);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SEMESTER_NOT_FOUND));
        
        if (requestDTO.getTuitionFeeId() != null) {
            TuitionFee fee = tuitionFeeRepository.findById(requestDTO.getTuitionFeeId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_FEE_NOT_FOUND));
            tuition.setTuitionFee(fee);
        }
        
        tuition.setStudent(student);
        tuition.setSemester(semester);
        tuition.setActive(true);
        tuition.setCreatedAt(LocalDateTime.now());
        
        return studentTuitionMapper.toResponseDTO(studentTuitionRepository.save(tuition));
    }

    @Override
    public Page<StudentTuitionResponseDTO> getByStudentId(UUID studentId, Pageable pageable) {
        return studentTuitionRepository.findByStudentId(studentId, pageable)
                .map(studentTuitionMapper::toResponseDTO);
    }

    @Override
    public Page<StudentTuitionResponseDTO> getBySemesterId(UUID semesterId, Pageable pageable) {
        return studentTuitionRepository.findBySemesterId(semesterId, pageable)
                .map(studentTuitionMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public StudentTuitionResponseDTO update(UUID id, StudentTuitionRequestDTO requestDTO) {
        StudentTuition tuition = studentTuitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_RECORD_NOT_FOUND));
        studentTuitionMapper.updateEntityFromDTO(requestDTO, tuition);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SEMESTER_NOT_FOUND));
        
        if (requestDTO.getTuitionFeeId() != null) {
            TuitionFee fee = tuitionFeeRepository.findById(requestDTO.getTuitionFeeId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_FEE_NOT_FOUND));
            tuition.setTuitionFee(fee);
        }
        
        tuition.setStudent(student);
        tuition.setSemester(semester);
        tuition.setUpdatedAt(LocalDateTime.now());
        
        return studentTuitionMapper.toResponseDTO(studentTuitionRepository.save(tuition));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentTuition tuition = studentTuitionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_RECORD_NOT_FOUND));
        tuition.softDelete("system");
        studentTuitionRepository.save(tuition);
    }
}
