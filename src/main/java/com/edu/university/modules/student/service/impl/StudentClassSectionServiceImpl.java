package com.edu.university.modules.student.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.student.dto.request.StudentClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.StudentClassSectionResponseDTO;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.entity.StudentClassSection;
import com.edu.university.modules.student.mapper.StudentClassSectionMapper;
import com.edu.university.modules.student.repository.StudentClassRepository;
import com.edu.university.modules.student.repository.StudentClassSectionRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.student.service.StudentClassSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentClassSectionServiceImpl implements StudentClassSectionService {

    private final StudentClassSectionRepository repository;
    private final StudentRepository studentRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentClassSectionMapper mapper;

    @Override
    @Transactional
    public StudentClassSectionResponseDTO addStudentToClass(StudentClassSectionRequestDTO requestDTO) {
        if(repository.existsByStudentIdAndStudentClassId(requestDTO.getStudentId(), requestDTO.getStudentClassesId())){
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Sinh viên đã có trong lớp này");
        }
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        StudentClass studentClass = studentClassRepository.findById(requestDTO.getStudentClassesId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học"));
                
        StudentClassSection entity = mapper.toEntity(requestDTO);
        entity.setStudent(student);
        entity.setStudentClass(studentClass);
        entity.setActive(true);
        
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentClassSectionResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentClassSectionResponseDTO> getByStudentId(UUID studentId, Pageable pageable) {
        return repository.findByStudentId(studentId, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentClassSectionResponseDTO> getByClassId(UUID studentClassesId, Pageable pageable) {
        return repository.findByStudentClassId(studentClassesId, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional
    public StudentClassSectionResponseDTO update(UUID id, StudentClassSectionRequestDTO requestDTO) {
        StudentClassSection entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy dữ liệu"));
        
        mapper.updateEntityFromDTO(requestDTO, entity);
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy sinh viên"));
        StudentClass studentClass = studentClassRepository.findById(requestDTO.getStudentClassesId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học"));
                
        entity.setStudent(student);
        entity.setStudentClass(studentClass);
        
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        StudentClassSection entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy dữ liệu"));
        entity.softDelete("system");
        repository.save(entity);
    }
}