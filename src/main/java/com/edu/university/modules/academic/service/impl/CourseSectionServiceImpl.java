package com.edu.university.modules.academic.service.impl;

import com.edu.university.common.dto.PageResponse;
import com.edu.university.common.exception.AppException;
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
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.MajorRepository;
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
    private final MajorRepository majorRepository;
    private final CourseSectionMapper courseSectionMapper;

    @Override
    @Transactional
    public CourseSectionResponseDTO create(CourseSectionRequestDTO requestDTO) {
        log.info("Creating course section with code: {}", requestDTO.getClassCode());
        if (courseSectionRepository.existsByClassCode(requestDTO.getClassCode())) {
            throw new AppException(ErrorCode.ALREADY_EXISTS, "Mã lớp học phần đã tồn tại");
        }

        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy học phần"));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));

        Major major = null;
        if (requestDTO.getMajorId() != null) {
            major = majorRepository.findById(requestDTO.getMajorId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành học"));
        }

        CourseSection courseSection = courseSectionMapper.toEntity(requestDTO);

        // Fix 500 error: sectionCode MUST NOT be null
        if (courseSection.getSectionCode() == null) {
            courseSection.setSectionCode("SEC_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        courseSection.setCourse(course);
        courseSection.setSemester(semester);
        courseSection.setMajor(major);
        courseSection.setIsActive(true);

        // Handle isSystem if provided
        if (requestDTO.getIsSystem() != null) {
            courseSection.setIsSystem(requestDTO.getIsSystem());
        }

        return courseSectionMapper.toResponseDTO(courseSectionRepository.save(courseSection));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseSectionResponseDTO> getAll(UUID departmentId, UUID majorId, Pageable pageable) {
        log.info("Getting course sections with pagination: {}, department: {}, major: {}", pageable, departmentId, majorId);
        Page<CourseSection> page;
        if (majorId != null) {
            page = courseSectionRepository.findByMajorId(majorId, pageable);
        } else if (departmentId != null) {
            page = courseSectionRepository.findByCourse_DepartmentId(departmentId, pageable);
        } else {
            page = courseSectionRepository.findAll(pageable);
        }
        return PageResponse.of(page.map(courseSectionMapper::toResponseDTO));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseSectionResponseDTO getById(UUID id) {
        log.info("Getting course section by id: {}", id);
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));
        return courseSectionMapper.toResponseDTO(courseSection);
    }

    @Override
    @Transactional
    public CourseSectionResponseDTO update(UUID id, CourseSectionRequestDTO requestDTO) {
        log.info("Updating course section with id: {}", id);
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));

        Course course = courseRepository.findById(requestDTO.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy học phần"));
        Semester semester = semesterRepository.findById(requestDTO.getSemesterId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));

        Major major = null;
        if (requestDTO.getMajorId() != null) {
            major = majorRepository.findById(requestDTO.getMajorId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy ngành học"));
        }

        // Protection for system classes - potentially restrict some fields
        if (Boolean.TRUE.equals(courseSection.getIsSystem()) && requestDTO.getIsSystem() != null
                && !requestDTO.getIsSystem()) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Không thể hủy bỏ trạng thái hệ thống của lớp học này");
        }

        courseSectionMapper.updateEntityFromDTO(requestDTO, courseSection);
        courseSection.setCourse(course);
        courseSection.setSemester(semester);
        courseSection.setMajor(major);

        if (requestDTO.getIsSystem() != null) {
            courseSection.setIsSystem(requestDTO.getIsSystem());
        }

        return courseSectionMapper.toResponseDTO(courseSectionRepository.save(courseSection));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting course section with id: {}", id);
        CourseSection courseSection = courseSectionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Không tìm thấy lớp học phần"));

        if (Boolean.TRUE.equals(courseSection.getIsSystem())) {
            throw new AppException(ErrorCode.BAD_REQUEST, "Đây là lớp học phần hệ thống, không thể xóa");
        }

        courseSection.softDelete("system");
        courseSectionRepository.save(courseSection);
    }
}
