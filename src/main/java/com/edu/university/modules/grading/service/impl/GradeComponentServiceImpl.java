package com.edu.university.modules.grading.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.grading.dto.request.GradeComponentRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeComponentResponseDTO;
import com.edu.university.modules.grading.entity.GradeComponent;
import com.edu.university.modules.grading.mapper.GradeComponentMapper;
import com.edu.university.modules.grading.repository.GradeComponentRepository;
import com.edu.university.modules.grading.service.GradeComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeComponentServiceImpl implements GradeComponentService {

    private final GradeComponentRepository gradeComponentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final GradeComponentMapper gradeComponentMapper;

    @Override
    @Transactional
    public GradeComponentResponseDTO create(GradeComponentRequestDTO requestDTO) {
        GradeComponent component = gradeComponentMapper.toEntity(requestDTO);
        CourseSection section = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
        component.setCourseSection(section);
        component.setActive(true);
        return gradeComponentMapper.toResponseDTO(gradeComponentRepository.save(component));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GradeComponentResponseDTO> getByCourseSectionId(UUID courseSectionId, Pageable pageable) {
        return gradeComponentRepository.findByCourseSectionId(courseSectionId, pageable)
                .map(gradeComponentMapper::toResponseDTO);
    }

    @Override
    @Transactional
    public GradeComponentResponseDTO update(UUID id, GradeComponentRequestDTO requestDTO) {
        GradeComponent component = gradeComponentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thành phần điểm"));
        gradeComponentMapper.updateEntityFromDTO(requestDTO, component);
        CourseSection section = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
        component.setCourseSection(section);
        return gradeComponentMapper.toResponseDTO(gradeComponentRepository.save(component));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GradeComponent component = gradeComponentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy thành phần điểm"));
        component.softDelete("system");
        gradeComponentRepository.save(component);
    }
}
