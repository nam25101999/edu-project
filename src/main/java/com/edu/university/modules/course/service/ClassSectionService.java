package com.edu.university.modules.course.service;

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

@Service
@RequiredArgsConstructor
public class ClassSectionService {

    private final ClassSectionRepository classSectionRepository;
    private final CourseRepository courseRepository;

    // 1. INJECT MAPPER VÀO ĐÂY
    private final ClassSectionMapper classSectionMapper;

    public Page<ClassSection> getAllClassSections(Pageable pageable) {
        return classSectionRepository.findAll(pageable);
    }

    public ClassSection getClassSectionById(UUID id) {
        return classSectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần với ID: " + id));
    }

    @Transactional
    public ClassSection createClassSection(ClassSectionRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + request.courseId()));

        // 2. DÙNG MAPPER CHUYỂN DTO -> ENTITY
        ClassSection classSection = classSectionMapper.toEntity(request);

        // 3. Gán thủ công thuộc tính Course (do đã ignore trong Mapper)
        classSection.setCourse(course);

        return classSectionRepository.save(classSection);
    }

    @Transactional
    public ClassSection updateClassSection(UUID id, ClassSectionRequest request) {
        ClassSection classSection = getClassSectionById(id);

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + request.courseId()));

        // 4. DÙNG MAPPER ĐỂ CẬP NHẬT TRỰC TIẾP VÀO ENTITY CŨ
        classSectionMapper.updateEntityFromDto(request, classSection);

        // Cập nhật lại khoá ngoại Course
        classSection.setCourse(course);

        return classSectionRepository.save(classSection);
    }

    @Transactional
    public void deleteClassSection(UUID id) {
        ClassSection classSection = getClassSectionById(id);
        classSectionRepository.delete(classSection);
    }
}