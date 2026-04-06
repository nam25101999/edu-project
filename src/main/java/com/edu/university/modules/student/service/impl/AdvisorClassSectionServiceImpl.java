package com.edu.university.modules.student.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import com.edu.university.modules.student.dto.request.AdvisorClassSectionRequestDTO;
import com.edu.university.modules.student.dto.response.AdvisorClassSectionResponseDTO;
import com.edu.university.modules.student.entity.AdvisorClassSection;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.mapper.AdvisorClassSectionMapper;
import com.edu.university.modules.student.repository.AdvisorClassSectionRepository;
import com.edu.university.modules.student.repository.StudentClassRepository;
import com.edu.university.modules.student.service.AdvisorClassSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdvisorClassSectionServiceImpl implements AdvisorClassSectionService {

    private final AdvisorClassSectionRepository repository;
    private final EmployeeRepository employeeRepository;
    private final StudentClassRepository studentClassRepository;
    private final AdvisorClassSectionMapper mapper;

    @Override
    @Transactional
    public AdvisorClassSectionResponseDTO assignAdvisorToClass(AdvisorClassSectionRequestDTO requestDTO) {
        if(repository.existsByAdvisorIdAndStudentClassId(requestDTO.getAdvisorId(), requestDTO.getStudentClassesId())){
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Cố vấn này đã được phân công cho lớp này");
        }
        
        AdvisorClassSection entity = mapper.toEntity(requestDTO);
        
        Employee advisor = employeeRepository.findById(requestDTO.getAdvisorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy cố vấn"));
        StudentClass studentClass = studentClassRepository.findById(requestDTO.getStudentClassesId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học"));
                
        entity.setAdvisor(advisor);
        entity.setStudentClass(studentClass);
        entity.setActive(true);
        if (requestDTO.getStartDate() != null) {
            entity.setStartDate(requestDTO.getStartDate().atStartOfDay());
        }
        if (requestDTO.getEndDate() != null) {
            entity.setEndDate(requestDTO.getEndDate().atStartOfDay());
        }
        
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdvisorClassSectionResponseDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdvisorClassSectionResponseDTO> getByAdvisorId(UUID advisorId, Pageable pageable) {
        return repository.findByAdvisorId(advisorId, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdvisorClassSectionResponseDTO> getByClassId(UUID studentClassesId, Pageable pageable) {
        return repository.findByStudentClassId(studentClassesId, pageable).map(mapper::toResponseDTO);
    }

    @Override
    @Transactional
    public AdvisorClassSectionResponseDTO update(UUID id, AdvisorClassSectionRequestDTO requestDTO) {
        AdvisorClassSection entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy dữ liệu"));
        
        mapper.updateEntityFromDTO(requestDTO, entity);
        
        Employee advisor = employeeRepository.findById(requestDTO.getAdvisorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy cố vấn"));
        StudentClass studentClass = studentClassRepository.findById(requestDTO.getStudentClassesId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học"));
                
        entity.setAdvisor(advisor);
        entity.setStudentClass(studentClass);
        
        if (requestDTO.getStartDate() != null) {
            entity.setStartDate(requestDTO.getStartDate().atStartOfDay());
        }
        if (requestDTO.getEndDate() != null) {
            entity.setEndDate(requestDTO.getEndDate().atStartOfDay());
        }
        
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AdvisorClassSection entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy dữ liệu"));
        entity.softDelete("system");
        repository.save(entity);
    }
}