package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.elearning.entity.Assignment;
import com.edu.university.modules.elearning.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseSectionRepository courseSectionRepository;

    @Transactional
    public Assignment createAssignment(UUID courseSectionId, String title, String description, LocalDateTime dueDate, Double maxScore, String attachmentUrl) {
        CourseSection courseSection = courseSectionRepository.findById(courseSectionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        Assignment assignment = Assignment.builder()
                .courseSection(courseSection)
                .title(title)
                .description(description)
                .dueDate(dueDate)
                .maxScore(maxScore)
                .attachmentUrl(attachmentUrl)
                .build();

        return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignmentsByCourseSection(UUID courseSectionId) {
        return assignmentRepository.findByCourseSectionId(courseSectionId);
    }
}
