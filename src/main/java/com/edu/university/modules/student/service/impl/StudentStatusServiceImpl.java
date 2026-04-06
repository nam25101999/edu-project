package com.edu.university.modules.student.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.student.dto.request.StudentStatusRequestDTO;
import com.edu.university.modules.student.dto.response.StudentStatusResponseDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentStatus;
import com.edu.university.modules.student.mapper.StudentStatusMapper;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.student.repository.StudentStatusRepository;
import com.edu.university.modules.student.service.StudentStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentStatusServiceImpl implements StudentStatusService {

    private final StudentStatusRepository repository;
    private final StudentRepository studentRepository;
    private final StudentStatusMapper mapper;

    @Override
    @Transactional
    public StudentStatusResponseDTO createStatus(StudentStatusRequestDTO requestDTO) {
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
                
        StudentStatus entity = mapper.toEntity(requestDTO);
        entity.setStudent(student);
        entity.setActive(true);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentStatusResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentStatusResponseDTO> getByStudentId(UUID studentId, Pageable pageable) {
        return repository.findByStudent_Id(studentId, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional
    public StudentStatusResponseDTO updateStatus(UUID id, StudentStatusRequestDTO requestDTO) {
        StudentStatus entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy trạng thái"));
        
        mapper.updateEntityFromDTO(requestDTO, entity);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        
        entity.setStudent(student);
        
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteStatus(UUID id) {
        StudentStatus entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy trạng thái"));
        entity.softDelete("system");
        repository.save(entity);
    }
}