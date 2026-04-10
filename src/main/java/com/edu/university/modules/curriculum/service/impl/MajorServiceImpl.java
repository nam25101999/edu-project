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
    private final com.edu.university.modules.hr.repository.DepartmentRepository departmentRepository;
    private final MajorMapper majorMapper;

    @Override
    @Transactional
    public MajorResponseDTO create(MajorRequestDTO requestDTO) {
        if (majorRepository.existsByMajorCode(requestDTO.getMajorCode())) {
            throw new BusinessException(ErrorCode.MAJOR_CODE_EXISTS);
        }

        Faculty faculty = null;
        if (requestDTO.getFacultyId() != null) {
            faculty = facultyRepository.findById(requestDTO.getFacultyId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.FACULTY_NOT_FOUND));
        }

        com.edu.university.modules.hr.entity.Department department = null;
        if (requestDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
            
            // Auto-resolve faculty if missing but department exists
            if (faculty == null) {
                final String deptCode = department.getCode();
                faculty = facultyRepository.findFirstByCode(deptCode)
                        .orElseThrow(() -> new BusinessException(ErrorCode.FACULTY_NOT_FOUND, 
                            "Không tìm thấy thông tin Khoa tương ứng với mã Phòng ban: " + deptCode));
            }
        }

        Major major = majorMapper.toEntity(requestDTO);
        major.setFaculty(faculty);
        major.setDepartment(department);
        major.setActive(true);

        return majorMapper.toResponseDTO(majorRepository.save(major));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MajorResponseDTO> getAll(String search, Pageable pageable) {
        return majorRepository.findPageWithRelations(normalizeSearch(search), pageable)
                .map(majorMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public MajorResponseDTO getById(UUID id) {
        Major major = majorRepository.findDetailById(id)
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

        if (requestDTO.getDepartmentId() != null) {
            com.edu.university.modules.hr.entity.Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
            major.setDepartment(department);
        } else {
            major.setDepartment(null);
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

    @Override
    @Transactional(readOnly = true)
    public java.util.List<MajorResponseDTO> getByDepartment(UUID departmentId) {
        return majorRepository.findByDepartmentId(departmentId).stream()
                .map(majorMapper::toResponseDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private String normalizeSearch(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return search.trim();
    }
}
