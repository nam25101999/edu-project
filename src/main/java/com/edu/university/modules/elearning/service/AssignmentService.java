package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.AppException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.entity.Assignment;
import com.edu.university.modules.elearning.dto.response.AssignmentResponseDTO;
import com.edu.university.modules.elearning.mapper.AssignmentMapper;
import com.edu.university.modules.elearning.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final AssignmentMapper assignmentMapper;

    @Transactional
    public AssignmentResponseDTO createAssignment(UUID courseSectionId, String title, String description, LocalDateTime dueDate, Double maxScore, String attachmentUrl) {
        CourseSection courseSection = courseSectionRepository.findById(courseSectionId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        Assignment assignment = Assignment.builder()
                .courseSection(courseSection)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .maxScore(maxScore)
                .attachmentUrl(attachmentUrl)
                .isActive(true)
                .build();

        return assignmentMapper.toResponseDTO(assignmentRepository.save(assignment));
    }

    public Page<AssignmentResponseDTO> getAssignmentsByCourseSection(UUID courseSectionId, Pageable pageable) {
        return assignmentRepository.findByCourseSectionId(courseSectionId, pageable)
                .map(assignmentMapper::toResponseDTO);
    }
}
