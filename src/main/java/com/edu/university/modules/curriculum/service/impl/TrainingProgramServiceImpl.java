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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã chương trình đào tạo đã tồn tại");
        }
        TrainingProgram trainingProgram = trainingProgramMapper.toEntity(requestDTO);
        if (requestDTO.getMajorId() != null) {
            Major major = majorRepository.findById(requestDTO.getMajorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành"));
            trainingProgram.setMajor(major);
        }
        if (requestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khoa"));
            trainingProgram.setDepartment(department);
        }
        trainingProgram.setActive(true);
        trainingProgram.setCreatedAt(LocalDateTime.now());
        return trainingProgramMapper.toResponseDTO(trainingProgramRepository.save(trainingProgram));
    }

    @Override
    public List<TrainingProgramResponseDTO> getAll() {
        return trainingProgramRepository.findAll().stream()
                .map(trainingProgramMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TrainingProgramResponseDTO getById(UUID id) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chương trình đào tạo"));
        return trainingProgramMapper.toResponseDTO(trainingProgram);
    }

    @Override
    @Transactional
    public TrainingProgramResponseDTO update(UUID id, TrainingProgramRequestDTO requestDTO) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chương trình đào tạo"));
        trainingProgramMapper.updateEntityFromDTO(requestDTO, trainingProgram);
        if (requestDTO.getMajorId() != null) {
            Major major = majorRepository.findById(requestDTO.getMajorId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành"));
            trainingProgram.setMajor(major);
        } else {
            trainingProgram.setMajor(null);
        }
        if (requestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoa"));
            trainingProgram.setDepartment(department);
        } else {
            trainingProgram.setDepartment(null);
        }
        trainingProgram.setUpdatedAt(LocalDateTime.now());
        return trainingProgramMapper.toResponseDTO(trainingProgramRepository.save(trainingProgram));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TrainingProgram trainingProgram = trainingProgramRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy chương trình đào tạo"));
        trainingProgram.softDelete("system");
        trainingProgramRepository.save(trainingProgram);
    }
}
