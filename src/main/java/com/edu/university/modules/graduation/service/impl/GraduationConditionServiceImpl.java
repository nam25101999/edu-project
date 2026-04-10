package com.edu.university.modules.graduation.service.impl;
 
import com.edu.university.common.exception.AppException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.graduation.dto.request.GraduationConditionRequestDTO;
import com.edu.university.modules.graduation.dto.response.GraduationConditionResponseDTO;
import com.edu.university.modules.graduation.entity.GraduationCondition;
import com.edu.university.modules.graduation.mapper.GraduationConditionMapper;
import com.edu.university.modules.graduation.repository.GraduationConditionRepository;
import com.edu.university.modules.graduation.service.GraduationConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.UUID;
 
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
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        condition.setTrainingProgram(program);
        condition.setActive(true);
        condition.setCreatedAt(LocalDateTime.now());
        return graduationConditionMapper.toResponseDTO(graduationConditionRepository.save(condition));
    }
 
    @Override
    @Transactional(readOnly = true)
    public Page<GraduationConditionResponseDTO> getAll(Pageable pageable) {
        return graduationConditionRepository.findAll(pageable)
                .map(graduationConditionMapper::toResponseDTO);
    }
 
    @Override
    @Transactional(readOnly = true)
    public GraduationConditionResponseDTO getById(UUID id) {
        GraduationCondition condition = graduationConditionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        return graduationConditionMapper.toResponseDTO(condition);
    }
 
    @Override
    @Transactional
    public GraduationConditionResponseDTO update(UUID id, GraduationConditionRequestDTO requestDTO) {
        GraduationCondition condition = graduationConditionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        graduationConditionMapper.updateEntityFromDTO(requestDTO, condition);
        TrainingProgram program = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        condition.setTrainingProgram(program);
        condition.setUpdatedAt(LocalDateTime.now());
        return graduationConditionMapper.toResponseDTO(graduationConditionRepository.save(condition));
    }
 
    @Override
    @Transactional
    public void delete(UUID id) {
        GraduationCondition condition = graduationConditionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        condition.softDelete("system");
        graduationConditionRepository.save(condition);
    }
}
