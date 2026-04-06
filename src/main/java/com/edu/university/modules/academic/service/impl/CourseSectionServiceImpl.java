package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.CourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.CourseSectionResponseDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.mapper.CourseSectionMapper;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.academic.service.CourseSectionService;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseSectionServiceImpl implements CourseSectionService {

    private final CourseSectionRepository courseSectionRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final CourseSectionMapper courseSectionMapper;

    @Override
    @Transactional
    public CourseSectionResponseDTO create(CourseSectionRequestDTO requestDTO) {
        log.info("Creating course section with code: {}", requestDTO.getClassCode());
        if (courseSectionRepository.existsByClassCode(requestDTO.getClassCode())) {
            throw new BusinessException(ErrorCode.COURSE_ALREADY_EXISTS, "Mã lớp học phần đã tồn tại");
        }
        
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SEMESTER_NOT_FOUND));

        CourseSection courseSection = courseSectionMapper.toEntity(requestDTO);
        courseSection.setCourse(course);
        courseSection.setSemester(semester);
        courseSection.setActive(true);
        
        return courseSectionMapper.toResponseDTO(courseSectionRepository.save(courseSection));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseSectionResponseDTO> getAll(Pageable pageable) {
        log.info("Getting all course sections with pagination: {}", pageable);
        return courseSectionRepository.findAll(pageable)
                .map(courseSectionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseSectionResponseDTO getById(UUID id) {
        log.info("Getting course section by id: {}", id);
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));
        return courseSectionMapper.toResponseDTO(courseSection);
    }

    @Override
    @Transactional
    public CourseSectionResponseDTO update(UUID id, CourseSectionRequestDTO requestDTO) {
        log.info("Updating course section with id: {}", id);
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));
        
        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SEMESTER_NOT_FOUND));

        courseSectionMapper.updateEntityFromDTO(requestDTO, courseSection);
        courseSection.setCourse(course);
        courseSection.setSemester(semester);
        
        return courseSectionMapper.toResponseDTO(courseSectionRepository.save(courseSection));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting course section with id: {}", id);
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));
        courseSection.softDelete("system");
        courseSectionRepository.save(courseSection);
    }
}
