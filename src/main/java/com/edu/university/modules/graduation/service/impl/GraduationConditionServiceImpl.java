package com.edu.university.modules.graduation.service.impl;

import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.graduation.dto.request.GraduationConditionRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationConditionResponseDTO;
import com.edu.university.modules.graduation.entity.GraduationCondition;
import com.edu.university.modules.graduation.mapper.GraduationConditionMapper;
import com.edu.university.modules.graduation.repository.GraduationConditionRepository;
import com.edu.university.modules.graduation.service.GraduationConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraduationConditionServiceImpl implements GraduationConditionService {

    private final GraduationConditionRepository graduationConditionRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final GraduationConditionMapper graduationConditionMapper;

    @Override
    @Transactional
    public GraduationConditionResponseDTO create(GraduationConditionRequestDTO requestDTO) {
        GraduationCondition condition = graduationConditionMapper.toEntity(requestDTO);
        TrainingProgram program = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình đào tạo"));
        condition.setTrainingProgram(program);
        condition.setActive(true);
        condition.setCreatedAt(LocalDateTime.now());
        return graduationConditionMapper.toResponseDTO(graduationConditionRepository.save(condition));
    }

    @Override
    public List<GraduationConditionResponseDTO> getAll() {
        return graduationConditionRepository.findAll().stream()
                .map(graduationConditionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GraduationConditionResponseDTO getById(UUID id) {
        GraduationCondition condition = graduationConditionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điều kiện tốt nghiệp"));
        return graduationConditionMapper.toResponseDTO(condition);
    }

    @Override
    @Transactional
    public GraduationConditionResponseDTO update(UUID id, GraduationConditionRequestDTO requestDTO) {
        GraduationCondition condition = graduationConditionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điều kiện tốt nghiệp"));
        graduationConditionMapper.updateEntityFromDTO(requestDTO, condition);
        TrainingProgram program = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình đào tạo"));
        condition.setTrainingProgram(program);
        condition.setUpdatedAt(LocalDateTime.now());
        return graduationConditionMapper.toResponseDTO(graduationConditionRepository.save(condition));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GraduationCondition condition = graduationConditionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điều kiện tốt nghiệp"));
        condition.softDelete("system");
        graduationConditionRepository.save(condition);
    }
}
