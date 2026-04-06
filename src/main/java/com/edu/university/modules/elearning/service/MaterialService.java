package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.entity.Material;
import com.edu.university.modules.elearning.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CourseSectionRepository courseSectionRepository;

    @Transactional
    public Material createMaterial(UUID courseSectionId, String title, String description, String fileUrl, String fileType, Long fileSize) {
        CourseSection courseSection = courseSectionRepository.findById(courseSectionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        Material material = Material.builder()
                .courseSection(courseSection)
                .title(title)
                .description(description)
                .fileUrl(fileUrl)
                .fileType(fileType)
                .fileSize(fileSize)
                .build();

        return materialRepository.save(material);
    }

    public List<Material> getMaterialsByCourseSection(UUID courseSectionId) {
        return materialRepository.findByCourseSectionId(courseSectionId);
    }
}
