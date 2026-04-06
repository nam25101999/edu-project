package com.edu.university.modules.curriculum.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.dto.request.TrainingProgramRequestDTO;
import com.edu.university.modules.curriculum.dto.response.TrainingProgramResponseDTO;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.mapper.TrainingProgramMapper;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.curriculum.service.TrainingProgramService;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingProgramServiceImpl implements TrainingProgramService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final MajorRepository majorRepository;
    private final DepartmentRepository departmentRepository;
    private final TrainingProgramMapper trainingProgramMapper;

    @Override
    @Transactional
    public TrainingProgramResponseDTO create(TrainingProgramRequestDTO requestDTO) {
        if (trainingProgramRepository.existsByProgramCode(requestDTO.getProgramCode())) {
            throw new BusinessException(ErrorCode.TRAINING_PROGRAM_CODE_EXISTS);
        }
        
        Major major = null;
        if (requestDTO.getMajorId() != null) {
            major = majorRepository.findById(requestDTO.getMajorId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND));
        }

        Department department = null;
        if (requestDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.FACULTY_NOT_FOUND)); // Or DEPARTMENT_NOT_FOUND if exists
        }

        TrainingProgram tp = trainingProgramMapper.toEntity(requestDTO);
        tp.setMajor(major);
        tp.setDepartment(department);
        tp.setActive(true);
        
        return trainingProgramMapper.toResponseDTO(trainingProgramRepository.save(tp));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingProgramResponseDTO> getAll(Pageable pageable) {
        return trainingProgramRepository.findAll(pageable)
                .map(trainingProgramMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingProgramResponseDTO getById(UUID id) {
        TrainingProgram tp = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_NOT_FOUND));
        return trainingProgramMapper.toResponseDTO(tp);
    }

    @Override
    @Transactional
    public TrainingProgramResponseDTO update(UUID id, TrainingProgramRequestDTO requestDTO) {
        TrainingProgram tp = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_NOT_FOUND));
        
        trainingProgramMapper.updateEntityFromDTO(requestDTO, tp);
        
        if (requestDTO.getMajorId() != null) {
            Major major = majorRepository.findById(requestDTO.getMajorId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND));
            tp.setMajor(major);
        }
        
        if (requestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.FACULTY_NOT_FOUND));
            tp.setDepartment(department);
        }
        
        return trainingProgramMapper.toResponseDTO(trainingProgramRepository.save(tp));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TrainingProgram tp = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_NOT_FOUND));
        tp.softDelete("system");
        trainingProgramRepository.save(tp);
    }
}
