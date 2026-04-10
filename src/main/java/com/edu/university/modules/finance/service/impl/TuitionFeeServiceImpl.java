package com.edu.university.modules.finance.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.finance.dto.request.TuitionFeeRequestDTO;
import com.edu.university.modules.finance.dto.response.TuitionFeeResponseDTO;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.mapper.TuitionFeeMapper;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import com.edu.university.modules.finance.service.TuitionFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TuitionFeeServiceImpl implements TuitionFeeService {

    private final TuitionFeeRepository tuitionFeeRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final TuitionFeeMapper tuitionFeeMapper;

    @Override
    @Transactional
    public TuitionFeeResponseDTO create(TuitionFeeRequestDTO requestDTO) {
        TuitionFee tuitionFee = tuitionFeeMapper.toEntity(requestDTO);
        TrainingProgram program = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_NOT_FOUND));
        tuitionFee.setTrainingProgram(program);
        tuitionFee.setActive(true);
        tuitionFee.setCreatedAt(LocalDateTime.now());
        return tuitionFeeMapper.toResponseDTO(tuitionFeeRepository.save(tuitionFee));
    }

    @Override
    public Page<TuitionFeeResponseDTO> getAll(Pageable pageable) {
        return tuitionFeeRepository.findAll(pageable)
                .map(tuitionFeeMapper::toResponseDTO);
    }

    @Override
    public TuitionFeeResponseDTO getById(UUID id) {
        TuitionFee tuitionFee = tuitionFeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_FEE_NOT_FOUND));
        return tuitionFeeMapper.toResponseDTO(tuitionFee);
    }

    @Override
    @Transactional
    public TuitionFeeResponseDTO update(UUID id, TuitionFeeRequestDTO requestDTO) {
        TuitionFee tuitionFee = tuitionFeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_FEE_NOT_FOUND));
        tuitionFeeMapper.updateEntityFromDTO(requestDTO, tuitionFee);
        TrainingProgram program = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new BusinessException(ErrorCode.TRAINING_PROGRAM_NOT_FOUND));
        tuitionFee.setTrainingProgram(program);
        tuitionFee.setUpdatedAt(LocalDateTime.now());
        return tuitionFeeMapper.toResponseDTO(tuitionFeeRepository.save(tuitionFee));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TuitionFee tuitionFee = tuitionFeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TUITION_FEE_NOT_FOUND));
        tuitionFee.softDelete("system");
        tuitionFeeRepository.save(tuitionFee);
    }
}
