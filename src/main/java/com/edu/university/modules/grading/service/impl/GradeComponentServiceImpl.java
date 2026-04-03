package com.edu.university.modules.grading.service.impl;

import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.grading.dto.request.GradeComponentRequestDTO;
import com.edu.university.modules.grading.dto.response.GradeComponentResponseDTO;
import com.edu.university.modules.grading.entity.GradeComponent;
import com.edu.university.modules.grading.mapper.GradeComponentMapper;
import com.edu.university.modules.grading.repository.GradeComponentRepository;
import com.edu.university.modules.grading.service.GradeComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần"));
        component.setCourseSection(section);
        component.setActive(true);
        component.setCreatedAt(LocalDateTime.now());
        return gradeComponentMapper.toResponseDTO(gradeComponentRepository.save(component));
    }

    @Override
    public List<GradeComponentResponseDTO> getByCourseSectionId(UUID courseSectionId) {
        return gradeComponentRepository.findByCourseSectionId(courseSectionId).stream()
                .map(gradeComponentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GradeComponentResponseDTO update(UUID id, GradeComponentRequestDTO requestDTO) {
        GradeComponent component = gradeComponentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phần điểm"));
        gradeComponentMapper.updateEntityFromDTO(requestDTO, component);
        CourseSection section = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần"));
        component.setCourseSection(section);
        component.setUpdatedAt(LocalDateTime.now());
        return gradeComponentMapper.toResponseDTO(gradeComponentRepository.save(component));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        GradeComponent component = gradeComponentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phần điểm"));
        component.softDelete("system");
        gradeComponentRepository.save(component);
    }
}
