package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.LecturerCourseClassRequestDTO;
import com.edu.university.modules.academic.dto.response.LecturerCourseClassResponseDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.LecturerCourseClass;
import com.edu.university.modules.academic.mapper.LecturerCourseClassMapper;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.LecturerCourseClassRepository;
import com.edu.university.modules.academic.service.LecturerCourseClassService;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.UserRepository;
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
public class LecturerCourseClassServiceImpl implements LecturerCourseClassService {

    private final LecturerCourseClassRepository lecturerCourseClassRepository;
    private final UserRepository userRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final LecturerCourseClassMapper lecturerCourseClassMapper;

    @Override
    @Transactional
    public LecturerCourseClassResponseDTO create(LecturerCourseClassRequestDTO requestDTO) {
        log.info("Assigning lecturer {} to course section {}", requestDTO.getLecturerId(), requestDTO.getCourseSectionId());
        
        Users lecturer = userRepository.findById(requestDTO.getLecturerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy giảng viên"));
        CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        LecturerCourseClass lecturerCourseClass = lecturerCourseClassMapper.toEntity(requestDTO);
        lecturerCourseClass.setLecturer(lecturer);
        lecturerCourseClass.setCourseSection(courseSection);
        lecturerCourseClass.setActive(true);
        
        return lecturerCourseClassMapper.toResponseDTO(lecturerCourseClassRepository.save(lecturerCourseClass));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LecturerCourseClassResponseDTO> getAll(Pageable pageable) {
        log.info("Getting all lecturer course class assignments with pagination: {}", pageable);
        return lecturerCourseClassRepository.findAll(pageable)
                .map(lecturerCourseClassMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public LecturerCourseClassResponseDTO getById(UUID id) {
        log.info("Getting lecturer course class assignment by id: {}", id);
        LecturerCourseClass lecturerCourseClass = lecturerCourseClassRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        return lecturerCourseClassMapper.toResponseDTO(lecturerCourseClass);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting lecturer course class assignment by id: {}", id);
        LecturerCourseClass lecturerCourseClass = lecturerCourseClassRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));
        lecturerCourseClass.softDelete("system");
        lecturerCourseClassRepository.save(lecturerCourseClass);
    }
}
