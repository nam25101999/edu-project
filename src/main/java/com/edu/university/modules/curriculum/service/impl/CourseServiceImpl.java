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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponseDTO create(CourseRequestDTO requestDTO) {
        if (courseRepository.existsByCourseCode(requestDTO.getCourseCode())) {
            throw new BusinessException(ErrorCode.COURSE_ALREADY_EXISTS);
        }
        
        Department department = null;
        if (requestDTO.getDepartmentId() != null) {
            department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoa"));
        }

        Course course = courseMapper.toEntity(requestDTO);
        course.setDepartment(department);
        course.setActive(true);
        
        return courseMapper.toResponseDTO(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponseDTO> getAll(String search, Pageable pageable) {
        return courseRepository.findPageWithDepartment(normalizeSearch(search), pageable)
                .map(courseMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getById(UUID id) {
        Course course = courseRepository.findDetailById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        return courseMapper.toResponseDTO(course);
    }

    @Override
    @Transactional
    public CourseResponseDTO update(UUID id, CourseRequestDTO requestDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        
        courseMapper.updateEntityFromDTO(requestDTO, course);
        
        if (requestDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(requestDTO.getDepartmentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy khoa"));
            course.setDepartment(department);
        } else {
            course.setDepartment(null);
        }
        
        return courseMapper.toResponseDTO(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        course.softDelete("system");
        courseRepository.save(course);
    }

    private String normalizeSearch(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }
        return search.trim();
    }
}
