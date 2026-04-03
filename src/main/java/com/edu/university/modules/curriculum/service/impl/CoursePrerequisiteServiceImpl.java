package com.edu.university.modules.curriculum.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.curriculum.dto.request.CoursePrerequisiteRequestDTO;
import com.edu.university.modules.curriculum.dto.response.CoursePrerequisiteResponseDTO;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.CoursePrerequisite;
import com.edu.university.modules.curriculum.mapper.CoursePrerequisiteMapper;
import com.edu.university.modules.curriculum.repository.CoursePrerequisiteRepository;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.service.CoursePrerequisiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoursePrerequisiteServiceImpl implements CoursePrerequisiteService {

    private final CoursePrerequisiteRepository coursePrerequisiteRepository;
    private final CourseRepository courseRepository;
    private final CoursePrerequisiteMapper coursePrerequisiteMapper;

    @Override
    @Transactional
    public CoursePrerequisiteResponseDTO create(CoursePrerequisiteRequestDTO requestDTO) {
        CoursePrerequisite coursePrerequisite = coursePrerequisiteMapper.toEntity(requestDTO);
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn học"));
        Course prerequisiteCourse = courseRepository.findById(requestDTO.getPrerequisiteCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy môn tiên quyết"));
        coursePrerequisite.setCourse(course);
        coursePrerequisite.setPrerequisiteCourse(prerequisiteCourse);
        coursePrerequisite.setActive(true);
        coursePrerequisite.setCreatedAt(LocalDateTime.now());
        return coursePrerequisiteMapper.toResponseDTO(coursePrerequisiteRepository.save(coursePrerequisite));
    }

    @Override
    public List<CoursePrerequisiteResponseDTO> getAll() {
        return coursePrerequisiteRepository.findAll().stream()
                .map(coursePrerequisiteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CoursePrerequisiteResponseDTO getById(UUID id) {
        CoursePrerequisite coursePrerequisite = coursePrerequisiteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông tin môn tiên quyết"));
        return coursePrerequisiteMapper.toResponseDTO(coursePrerequisite);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CoursePrerequisite coursePrerequisite = coursePrerequisiteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thông tin môn tiên quyết"));
        coursePrerequisite.softDelete("system");
        coursePrerequisiteRepository.save(coursePrerequisite);
    }
}
