package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.AcademicYearRequestDTO;
import com.edu.university.modules.academic.dto.response.AcademicYearResponseDTO;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.mapper.AcademicYearMapper;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.academic.service.AcademicYearService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearMapper academicYearMapper;

    @Override
    @Transactional
    public AcademicYearResponseDTO create(AcademicYearRequestDTO requestDTO) {
        log.info("Creating academic year with code: {}", requestDTO.getAcademicCode());
        if (academicYearRepository.existsByAcademicCode(requestDTO.getAcademicCode())) {
            throw new BusinessException(ErrorCode.ACADEMIC_YEAR_CODE_EXISTS);
        }
        AcademicYear academicYear = academicYearMapper.toEntity(requestDTO);
        academicYear.setActive(true);
        return academicYearMapper.toResponseDTO(academicYearRepository.save(academicYear));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcademicYearResponseDTO> getAll(Pageable pageable) {
        log.info("Getting all academic years with pagination: {}", pageable);
        return academicYearRepository.findAll(pageable)
                .map(academicYearMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicYearResponseDTO getById(UUID id) {
        log.info("Getting academic year by id: {}", id);
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACADEMIC_YEAR_NOT_FOUND));
        return academicYearMapper.toResponseDTO(academicYear);
    }

    @Override
    @Transactional
    public AcademicYearResponseDTO update(UUID id, AcademicYearRequestDTO requestDTO) {
        log.info("Updating academic year with id: {}", id);
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACADEMIC_YEAR_NOT_FOUND));
        
        academicYearMapper.updateEntityFromDTO(requestDTO, academicYear);
        return academicYearMapper.toResponseDTO(academicYearRepository.save(academicYear));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting academic year with id: {}", id);
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACADEMIC_YEAR_NOT_FOUND));
        academicYear.softDelete("system");
        academicYearRepository.save(academicYear);
    }
}
