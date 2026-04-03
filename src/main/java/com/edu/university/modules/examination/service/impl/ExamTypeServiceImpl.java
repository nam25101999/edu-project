package com.edu.university.modules.examination.service.impl;

import com.edu.university.modules.examination.dto.request.ExamTypeRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamTypeResponseDTO;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.mapper.ExamTypeMapper;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import com.edu.university.modules.examination.service.ExamTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamTypeServiceImpl implements ExamTypeService {

    private final ExamTypeRepository examTypeRepository;
    private final ExamTypeMapper examTypeMapper;

    @Override
    @Transactional
    public ExamTypeResponseDTO create(ExamTypeRequestDTO requestDTO) {
        ExamType examType = examTypeMapper.toEntity(requestDTO);
        examType.setActive(true);
        examType.setCreatedAt(LocalDateTime.now());
        return examTypeMapper.toResponseDTO(examTypeRepository.save(examType));
    }

    @Override
    public List<ExamTypeResponseDTO> getAll() {
        return examTypeRepository.findAll().stream()
                .map(examTypeMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamTypeResponseDTO getById(UUID id) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại kỳ thi"));
        return examTypeMapper.toResponseDTO(examType);
    }

    @Override
    @Transactional
    public ExamTypeResponseDTO update(UUID id, ExamTypeRequestDTO requestDTO) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại kỳ thi"));
        examTypeMapper.updateEntityFromDTO(requestDTO, examType);
        examType.setUpdatedAt(LocalDateTime.now());
        return examTypeMapper.toResponseDTO(examTypeRepository.save(examType));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại kỳ thi"));
        examType.softDelete("system");
        examTypeRepository.save(examType);
    }
}
