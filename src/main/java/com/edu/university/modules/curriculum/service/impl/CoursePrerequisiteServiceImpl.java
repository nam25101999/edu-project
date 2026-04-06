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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoursePrerequisiteServiceImpl implements CoursePrerequisiteService {

    private final CoursePrerequisiteRepository coursePrerequisiteRepository;
    private final CourseRepository courseRepository;
    private final CoursePrerequisiteMapper coursePrerequisiteMapper;

    @Override
    @Transactional
    public CoursePrerequisiteResponseDTO create(CoursePrerequisiteRequestDTO requestDTO) {
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        Course prerequisiteCourse = courseRepository.findById(requestDTO.getPrerequisiteCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Không tìm thấy môn tiên quyết"));

        if (requestDTO.getCourseId().equals(requestDTO.getPrerequisiteCourseId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Môn học không thể tự là môn tiên quyết của chính nó");
        }

        CoursePrerequisite cp = coursePrerequisiteMapper.toEntity(requestDTO);
        cp.setCourse(course);
        cp.setPrerequisiteCourse(prerequisiteCourse);
        cp.setActive(true);

        return coursePrerequisiteMapper.toResponseDTO(coursePrerequisiteRepository.save(cp));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CoursePrerequisiteResponseDTO> getAll(Pageable pageable) {
        return coursePrerequisiteRepository.findAll(pageable)
                .map(coursePrerequisiteMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CoursePrerequisiteResponseDTO getById(UUID id) {
        CoursePrerequisite cp = coursePrerequisiteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_PREREQUISITE_NOT_FOUND));
        return coursePrerequisiteMapper.toResponseDTO(cp);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        CoursePrerequisite cp = coursePrerequisiteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_PREREQUISITE_NOT_FOUND));
        cp.softDelete("system");
        coursePrerequisiteRepository.save(cp);
    }
}
