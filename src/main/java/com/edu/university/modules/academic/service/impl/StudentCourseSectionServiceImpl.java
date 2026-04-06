package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.dto.request.StudentCourseSectionRequestDTO;
import com.edu.university.modules.academic.dto.response.StudentCourseSectionResponseDTO;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.StudentCourseSection;
import com.edu.university.modules.academic.mapper.StudentCourseSectionMapper;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.StudentCourseSectionRepository;
import com.edu.university.modules.academic.service.StudentCourseSectionService;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentCourseSectionServiceImpl implements StudentCourseSectionService {

    private final StudentCourseSectionRepository studentCourseSectionRepository;
    private final StudentRepository studentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final StudentCourseSectionMapper studentCourseSectionMapper;

    @Override
    @Transactional
    public StudentCourseSectionResponseDTO create(StudentCourseSectionRequestDTO requestDTO) {
        log.info("Student {} registering for course section {}", requestDTO.getStudentId(), requestDTO.getCourseSectionId());
        
        Student student = studentRepository.findById(requestDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        CourseSection courseSection = courseSectionRepository.findById(requestDTO.getCourseSectionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND));

        if (studentCourseSectionRepository.findByStudentIdAndCourseSectionId(requestDTO.getStudentId(), requestDTO.getCourseSectionId()).isPresent()) {
            throw new BusinessException(ErrorCode.ALREADY_ENROLLED);
        }

        StudentCourseSection studentCourseSection = studentCourseSectionMapper.toEntity(requestDTO);
        studentCourseSection.setStudent(student);
        studentCourseSection.setCourseSection(courseSection);
        studentCourseSection.setActive(true);
        if (studentCourseSection.getRegisteredAt() == null) {
            studentCourseSection.setRegisteredAt(LocalDateTime.now());
        }
        
        return studentCourseSectionMapper.toResponseDTO(studentCourseSectionRepository.save(studentCourseSection));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentCourseSectionResponseDTO> getAll(Pageable pageable) {
        log.info("Getting all student course section registrations with pagination: {}", pageable);
        return studentCourseSectionRepository.findAll(pageable)
                .map(studentCourseSectionMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentCourseSectionResponseDTO getById(UUID id) {
        log.info("Getting student course section registration by id: {}", id);
        StudentCourseSection studentCourseSection = studentCourseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));
        return studentCourseSectionMapper.toResponseDTO(studentCourseSection);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting student course section registration by id: {}", id);
        StudentCourseSection studentCourseSection = studentCourseSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));
        studentCourseSection.softDelete("system");
        studentCourseSectionRepository.save(studentCourseSection);
    }
}
