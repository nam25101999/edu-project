package com.edu.university.modules.curriculum.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.dto.request.CourseRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CourseResponseDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.mapper.CourseMapper;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.service.CourseService;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponseDTO create(CourseRequestDTO requestDTO) {
        if (courseRepository.existsByCode(requestDTO.getCode())) {
            throw new BusinessException(ErrorCode.ALREADY_EXISTS, "Mã môn học đã tồn tại");
        }
        Course course = courseMapper.toEntity(requestDTO);
        if (requestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoa"));
            course.setDepartment(department);
        }
        course.setActive(true);
        course.setCreatedAt(LocalDateTime.now());
        return courseMapper.toResponseDTO(courseRepository.save(course));
    }

    @Override
    public List<CourseResponseDTO> getAll() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDTO getById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học"));
        return courseMapper.toResponseDTO(course);
    }

    @Override
    @Transactional
    public CourseResponseDTO update(UUID id, CourseRequestDTO requestDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học"));
        courseMapper.updateEntityFromDTO(requestDTO, course);
        if (requestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khoa"));
            course.setDepartment(department);
        } else {
            course.setDepartment(null);
        }
        course.setUpdatedAt(LocalDateTime.now());
        return courseMapper.toResponseDTO(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học"));
        course.softDelete("system");
        courseRepository.save(course);
    }
}
