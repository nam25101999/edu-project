package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.AppException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.entity.Material;
import com.edu.university.modules.elearning.dto.response.MaterialResponseDTO;
import com.edu.university.modules.elearning.mapper.MaterialMapper;
import com.edu.university.modules.elearning.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final MaterialMapper materialMapper;

    @Transactional
    public MaterialResponseDTO createMaterial(UUID courseSectionId, String title, String description, String fileUrl, String fileType, Long fileSize) {
        CourseSection courseSection = courseSectionRepository.findById(courseSectionId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        Material material = Material.builder()
                .courseSection(courseSection)
                .title(title)
                .description(description)
                .fileUrl(fileUrl)
                .fileType(fileType)
                .fileSize(fileSize)
                .isActive(true)
                .build();

        return materialMapper.toResponseDTO(materialRepository.save(material));
    }

    public Page<MaterialResponseDTO> getMaterialsByCourseSection(UUID courseSectionId, Pageable pageable) {
        return materialRepository.findByCourseSectionId(courseSectionId, pageable)
                .map(materialMapper::toResponseDTO);
    }
}
