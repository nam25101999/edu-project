package com.edu.university.modules.grading.service.impl;

import com.edu.university.modules.grading.dto.request.GradeScaleRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeScaleResponseDTO;
import com.edu.university.modules.grading.entity.GradeScale;
import com.edu.university.modules.grading.mapper.GradeScaleMapper;
import com.edu.university.modules.grading.repository.GradeScaleRepository;
import com.edu.university.modules.grading.service.GradeScaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        scale.setCreatedAt(LocalDateTime.now());
        return gradeScaleMapper.toResponseDTO(gradeScaleRepository.save(scale));
    }

    @Override
    public List<GradeScaleResponseDTO> getAll() {
        return gradeScaleRepository.findAll().stream()
                .map(gradeScaleMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GradeScaleResponseDTO getById(UUID id) {
        GradeScale scale = gradeScaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thang điểm"));
        return gradeScaleMapper.toResponseDTO(scale);
    }

    @Override
    @Transactional
    public GradeScaleResponseDTO update(UUID id, GradeScaleRequestDTO requestDTO) {
        GradeScale scale = gradeScaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thang điểm"));
        gradeScaleMapper.updateEntityFromDTO(requestDTO, scale);
        scale.setUpdatedAt(LocalDateTime.now());
        return gradeScaleMapper.toResponseDTO(gradeScaleRepository.save(scale));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GradeScale scale = gradeScaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thang điểm"));
        scale.softDelete("system");
        gradeScaleRepository.save(scale);
    }
}
