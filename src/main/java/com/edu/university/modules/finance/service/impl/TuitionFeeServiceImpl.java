package com.edu.university.modules.finance.service.impl;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình đào tạo"));
        tuitionFee.setTrainingProgram(program);
        tuitionFee.setActive(true);
        tuitionFee.setCreatedAt(LocalDateTime.now());
        return tuitionFeeMapper.toResponseDTO(tuitionFeeRepository.save(tuitionFee));
    }

    @Override
    public List<TuitionFeeResponseDTO> getAll() {
        return tuitionFeeRepository.findAll().stream()
                .map(tuitionFeeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TuitionFeeResponseDTO getById(UUID id) {
        TuitionFee tuitionFee = tuitionFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy định mức học phí"));
        return tuitionFeeMapper.toResponseDTO(tuitionFee);
    }

    @Override
    @Transactional
    public TuitionFeeResponseDTO update(UUID id, TuitionFeeRequestDTO requestDTO) {
        TuitionFee tuitionFee = tuitionFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy định mức học phí"));
        tuitionFeeMapper.updateEntityFromDTO(requestDTO, tuitionFee);
        TrainingProgram program = trainingProgramRepository.findById(requestDTO.getTrainingProgramId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chương trình đào tạo"));
        tuitionFee.setTrainingProgram(program);
        tuitionFee.setUpdatedAt(LocalDateTime.now());
        return tuitionFeeMapper.toResponseDTO(tuitionFeeRepository.save(tuitionFee));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TuitionFee tuitionFee = tuitionFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy định mức học phí"));
        tuitionFee.softDelete("system");
        tuitionFeeRepository.save(tuitionFee);
    }
}
