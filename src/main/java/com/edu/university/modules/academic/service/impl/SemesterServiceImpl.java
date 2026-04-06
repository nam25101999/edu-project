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
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final SemesterMapper semesterMapper;

    @Override
    @Transactional
    public SemesterResponseDTO create(SemesterRequestDTO requestDTO) {
        log.info("Creating semester with code: {}", requestDTO.getSemesterCode());
        if (semesterRepository.existsBySemesterCode(requestDTO.getSemesterCode())) {
            throw new BusinessException(ErrorCode.SEMESTER_CODE_EXISTS);
        }
        Semester semester = semesterMapper.toEntity(requestDTO);
        semester.setActive(true);
        return semesterMapper.toResponseDTO(semesterRepository.save(semester));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SemesterResponseDTO> getAll(Pageable pageable) {
        log.info("Getting all semesters with pagination: {}", pageable);
        return semesterRepository.findAll(pageable)
                .map(semesterMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SemesterResponseDTO getById(UUID id) {
        log.info("Getting semester by id: {}", id);
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEMESTER_NOT_FOUND));
        return semesterMapper.toResponseDTO(semester);
    }

    @Override
    @Transactional
    public SemesterResponseDTO update(UUID id, SemesterRequestDTO requestDTO) {
        log.info("Updating semester with id: {}", id);
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEMESTER_NOT_FOUND));
        
        semesterMapper.updateEntityFromDTO(requestDTO, semester);
        return semesterMapper.toResponseDTO(semesterRepository.save(semester));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting semester with id: {}", id);
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEMESTER_NOT_FOUND));
        semester.softDelete("system");
        semesterRepository.save(semester);
    }
}
