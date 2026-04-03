package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.dto.response.SemesterResponseDTO;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.mapper.SemesterMapper;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.academic.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final SemesterMapper semesterMapper;

    @Override
    @Transactional
    public SemesterResponseDTO create(SemesterRequestDTO requestDTO) {
        if (semesterRepository.existsBySemesterCode(requestDTO.getSemesterCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã học kỳ đã tồn tại");
        }
        Semester semester = semesterMapper.toEntity(requestDTO);
        semester.setActive(true);
        semester.setCreatedAt(LocalDateTime.now());
        return semesterMapper.toResponseDTO(semesterRepository.save(semester));
    }

    @Override
    public List<SemesterResponseDTO> getAll() {
        return semesterRepository.findAll().stream()
                .map(semesterMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SemesterResponseDTO getById(UUID id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));
        return semesterMapper.toResponseDTO(semester);
    }

    @Override
    @Transactional
    public SemesterResponseDTO update(UUID id, SemesterRequestDTO requestDTO) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));
        semesterMapper.updateEntityFromDTO(requestDTO, semester);
        semester.setUpdatedAt(LocalDateTime.now());
        return semesterMapper.toResponseDTO(semesterRepository.save(semester));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));
        semester.softDelete("system");
        semesterRepository.save(semester);
    }
}
