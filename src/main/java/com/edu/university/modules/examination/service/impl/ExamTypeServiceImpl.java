package com.edu.university.modules.examination.service.impl;
 
import com.edu.university.common.exception.AppException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.examination.dto.request.ExamTypeRequestDTO;
import com.edu.university.modules.examination.dto.response.ExamTypeResponseDTO;
import com.edu.university.modules.examination.entity.ExamType;
import com.edu.university.modules.examination.mapper.ExamTypeMapper;
import com.edu.university.modules.examination.repository.ExamTypeRepository;
import com.edu.university.modules.examination.service.ExamTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.UUID;
 
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
    @Transactional(readOnly = true)
    public Page<ExamTypeResponseDTO> getAll(Pageable pageable) {
        return examTypeRepository.findAll(pageable)
                .map(examTypeMapper::toResponseDTO);
    }
 
    @Override
    @Transactional(readOnly = true)
    public ExamTypeResponseDTO getById(UUID id) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        return examTypeMapper.toResponseDTO(examType);
    }
 
    @Override
    @Transactional
    public ExamTypeResponseDTO update(UUID id, ExamTypeRequestDTO requestDTO) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        examTypeMapper.updateEntityFromDTO(requestDTO, examType);
        examType.setUpdatedAt(LocalDateTime.now());
        return examTypeMapper.toResponseDTO(examTypeRepository.save(examType));
    }
 
    @Override
    @Transactional
    public void delete(UUID id) {
        ExamType examType = examTypeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DATA_NOT_FOUND));
        examType.softDelete("system");
        examTypeRepository.save(examType);
    }
}
