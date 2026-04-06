package com.edu.university.modules.grading.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.grading.dto.request.GradeScaleRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeScaleResponseDTO;
import com.edu.university.modules.grading.entity.GradeScale;
import com.edu.university.modules.grading.mapper.GradeScaleMapper;
import com.edu.university.modules.grading.repository.GradeScaleRepository;
import com.edu.university.modules.grading.service.GradeScaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeScaleServiceImpl implements GradeScaleService {

    private final GradeScaleRepository gradeScaleRepository;
    private final GradeScaleMapper gradeScaleMapper;

    @Override
    @Transactional
    public GradeScaleResponseDTO create(GradeScaleRequestDTO requestDTO) {
        GradeScale scale = gradeScaleMapper.toEntity(requestDTO);
        scale.setActive(true);
        return gradeScaleMapper.toResponseDTO(gradeScaleRepository.save(scale));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradeScaleResponseDTO> getAll(Pageable pageable) {
        return gradeScaleRepository.findAll(pageable)
                .map(gradeScaleMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public GradeScaleResponseDTO getById(UUID id) {
        GradeScale scale = gradeScaleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thang điểm"));
        return gradeScaleMapper.toResponseDTO(scale);
    }

    @Override
    @Transactional
    public GradeScaleResponseDTO update(UUID id, GradeScaleRequestDTO requestDTO) {
        GradeScale scale = gradeScaleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thang điểm"));
        gradeScaleMapper.updateEntityFromDTO(requestDTO, scale);
        return gradeScaleMapper.toResponseDTO(gradeScaleRepository.save(scale));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GradeScale scale = gradeScaleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thang điểm"));
        scale.softDelete("system");
        gradeScaleRepository.save(scale);
    }
}
