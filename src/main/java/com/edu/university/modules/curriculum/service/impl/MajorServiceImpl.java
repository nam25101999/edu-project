package com.edu.university.modules.curriculum.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.dto.request.MajorRequestDTO;
import com.edu.university.modules.curriculum.dto.response.MajorResponseDTO;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.mapper.MajorMapper;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.service.MajorService;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.hr.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MajorServiceImpl implements MajorService {

    private final MajorRepository majorRepository;
    private final FacultyRepository facultyRepository;
    private final MajorMapper majorMapper;

    @Override
    @Transactional
    public MajorResponseDTO create(MajorRequestDTO requestDTO) {
        if (majorRepository.existsByCode(requestDTO.getCode())) {
            throw new BusinessException(ErrorCode.MAJOR_CODE_EXISTS);
        }

        Faculty faculty = null;
        if (requestDTO.getFacultyId() != null) {
            faculty = facultyRepository.findById(requestDTO.getFacultyId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.FACULTY_NOT_FOUND));
        }

        Major major = majorMapper.toEntity(requestDTO);
        major.setFaculty(faculty);
        major.setActive(true);

        return majorMapper.toResponseDTO(majorRepository.save(major));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MajorResponseDTO> getAll(Pageable pageable) {
        return majorRepository.findAll(pageable)
                .map(majorMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public MajorResponseDTO getById(UUID id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND));
        return majorMapper.toResponseDTO(major);
    }

    @Override
    @Transactional
    public MajorResponseDTO update(UUID id, MajorRequestDTO requestDTO) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND));

        majorMapper.updateEntityFromDTO(requestDTO, major);

        if (requestDTO.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(requestDTO.getFacultyId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.FACULTY_NOT_FOUND));
            major.setFaculty(faculty);
        } else {
            major.setFaculty(null);
        }

        return majorMapper.toResponseDTO(majorRepository.save(major));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND));
        major.softDelete("system");
        majorRepository.save(major);
    }
}
