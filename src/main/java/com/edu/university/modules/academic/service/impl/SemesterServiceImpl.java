package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.SemesterRequestDTO;
import com.edu.university.modules.academic.dto.response.SemesterResponseDTO;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.mapper.SemesterMapper;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.academic.service.SemesterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SemesterMapper semesterMapper;
    private final AcademicCalendarStructureService academicCalendarStructureService;

    @Override
    @Transactional
    public SemesterResponseDTO create(SemesterRequestDTO requestDTO) {
        log.info("Creating semester with code: {}", requestDTO.getSemesterCode());
        if (semesterRepository.existsBySemesterCode(requestDTO.getSemesterCode())) {
            throw new BusinessException(ErrorCode.SEMESTER_CODE_EXISTS);
        }
        Semester semester = semesterMapper.toEntity(requestDTO);
        semester.setAcademicYear(resolveAcademicYear(requestDTO.getAcademicYear()));
        semester.setIsActive(true);
        return semesterMapper.toResponseDTO(semesterRepository.save(semester));
    }

    @Override
    @Transactional
    public Page<SemesterResponseDTO> getAll(String academicYear, Pageable pageable) {
        log.info("Getting all semesters with academicYear: {} and pagination: {}", academicYear, pageable);
        academicCalendarStructureService.ensureStandardStructure();
        
        Page<Semester> semesters;
        if (academicYear != null && !academicYear.isEmpty()) {
            semesters = semesterRepository.findByAcademicYear_AcademicYear(academicYear, pageable);
        } else {
            semesters = semesterRepository.findByAcademicYear_StartDateGreaterThanEqual(
                    LocalDate.of(AcademicCalendarStructureService.ACADEMIC_START_YEAR, 1, 1),
                    pageable);
        }
        
        return semesters.map(semesterMapper::toResponseDTO);
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
        semester.setAcademicYear(resolveAcademicYear(requestDTO.getAcademicYear()));
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

    private AcademicYear resolveAcademicYear(String academicYearValue) {
        return academicYearRepository.findByAcademicYear(academicYearValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Khong tim thay nam hoc"));
    }
}
