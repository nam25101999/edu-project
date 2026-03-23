package com.edu.university.modules.course.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.course.dto.ClassSectionDtos.ClassSectionRequest;
import com.edu.university.modules.course.entity.ClassSection;
import com.edu.university.modules.course.entity.Course;
import com.edu.university.modules.course.mapper.ClassSectionMapper;
import com.edu.university.modules.course.repository.ClassSectionRepository;
import com.edu.university.modules.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service quản lý lớp học phần.
 * Đã chuẩn hóa lỗi theo Enterprise Standard (ErrorCode CRS, SYS).
 */
@Service
@RequiredArgsConstructor
public class ClassSectionService {

    private final ClassSectionRepository classSectionRepository;
    private final CourseRepository courseRepository;
    private final ClassSectionMapper classSectionMapper;

    @LogAction(action = "VIEW_ALL_CLASS_SECTIONS", entityName = "CLASS_SECTION")
    public Page<ClassSection> getAllClassSections(Pageable pageable) {
        return classSectionRepository.findAll(pageable);
    }

    @LogAction(action = "VIEW_CLASS_SECTION", entityName = "CLASS_SECTION")
    public ClassSection getClassSectionById(UUID id) {
        return classSectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_SECTION_NOT_FOUND, "Không tìm thấy lớp học phần với ID: " + id));
    }

    @LogAction(action = "CREATE_CLASS_SECTION", entityName = "CLASS_SECTION")
    @Transactional
    public ClassSection createClassSection(ClassSectionRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        // Dùng Mapper chuyển DTO -> Entity
        ClassSection classSection = classSectionMapper.toEntity(request);
        classSection.setCourse(course);

        return classSectionRepository.save(classSection);
    }

    @LogAction(action = "UPDATE_CLASS_SECTION", entityName = "CLASS_SECTION")
    @Transactional
    public ClassSection updateClassSection(UUID id, ClassSectionRequest request) {
        ClassSection classSection = getClassSectionById(id);

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        // Cập nhật trực tiếp vào Entity cũ qua Mapper
        classSectionMapper.updateEntityFromDto(request, classSection);
        classSection.setCourse(course);

        return classSectionRepository.save(classSection);
    }

    @LogAction(action = "DELETE_CLASS_SECTION", entityName = "CLASS_SECTION")
    @Transactional
    public void deleteClassSection(UUID id) {
        ClassSection classSection = getClassSectionById(id);
        classSectionRepository.delete(classSection);
    }
}