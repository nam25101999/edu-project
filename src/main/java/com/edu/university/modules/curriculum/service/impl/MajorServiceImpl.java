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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã ngành đã tồn tại");
        }
        Major major = majorMapper.toEntity(requestDTO);
        if (requestDTO.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(requestDTO.getFacultyId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoa"));
            major.setFaculty(faculty);
        }
        major.setActive(true);
        major.setCreatedAt(LocalDateTime.now());
        return majorMapper.toResponseDTO(majorRepository.save(major));
    }

    @Override
    public List<MajorResponseDTO> getAll() {
        return majorRepository.findAll().stream()
                .map(majorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MajorResponseDTO getById(UUID id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành"));
        return majorMapper.toResponseDTO(major);
    }

    @Override
    @Transactional
    public MajorResponseDTO update(UUID id, MajorRequestDTO requestDTO) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành"));
        majorMapper.updateEntityFromDTO(requestDTO, major);
        if (requestDTO.getFacultyId() != null) {
            Faculty faculty = facultyRepository.findById(requestDTO.getFacultyId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoa"));
            major.setFaculty(faculty);
        } else {
            major.setFaculty(null);
        }
        major.setUpdatedAt(LocalDateTime.now());
        return majorMapper.toResponseDTO(majorRepository.save(major));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành"));
        major.softDelete("system");
        majorRepository.save(major);
    }
}
