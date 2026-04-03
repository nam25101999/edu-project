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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicYearServiceImpl implements AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearMapper academicYearMapper;

    @Override
    @Transactional
    public AcademicYearResponseDTO create(AcademicYearRequestDTO requestDTO) {
        if (academicYearRepository.existsByAcademicCode(requestDTO.getAcademicCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã năm học đã tồn tại");
        }
        AcademicYear academicYear = academicYearMapper.toEntity(requestDTO);
        academicYear.setActive(true);
        academicYear.setCreatedAt(LocalDateTime.now());
        return academicYearMapper.toResponseDTO(academicYearRepository.save(academicYear));
    }

    @Override
    public List<AcademicYearResponseDTO> getAll() {
        return academicYearRepository.findAll().stream()
                .map(academicYearMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AcademicYearResponseDTO getById(UUID id) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy năm học"));
        return academicYearMapper.toResponseDTO(academicYear);
    }

    @Override
    @Transactional
    public AcademicYearResponseDTO update(UUID id, AcademicYearRequestDTO requestDTO) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy năm học"));
        academicYearMapper.updateEntityFromDTO(requestDTO, academicYear);
        academicYear.setUpdatedAt(LocalDateTime.now());
        return academicYearMapper.toResponseDTO(academicYearRepository.save(academicYear));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy năm học"));
        academicYear.softDelete("system");
        academicYearRepository.save(academicYear);
    }
}
