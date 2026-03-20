package com.edu.university.service;

import com.edu.university.dto.ClassSectionDtos.ClassSectionRequest;
import com.edu.university.entity.ClassSection;
import com.edu.university.entity.Course;
import com.edu.university.repository.ClassSectionRepository;
import com.edu.university.repository.CourseRepository;
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

        ClassSection classSection = ClassSection.builder()
                .course(course)
                .lecturerId(request.lecturerId())
                .semester(request.semester())
                .year(request.year())
                .schedule(request.schedule())
                .room(request.room())
                .maxStudents(request.maxStudents())
                .build();

        return classSectionRepository.save(classSection);
    }

    @Transactional
    public ClassSection updateClassSection(UUID id, ClassSectionRequest request) {
        ClassSection classSection = getClassSectionById(id);

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + request.courseId()));

        classSection.setCourse(course);
        classSection.setLecturerId(request.lecturerId());
        classSection.setSemester(request.semester());
        classSection.setYear(request.year());
        classSection.setSchedule(request.schedule());
        classSection.setRoom(request.room());
        classSection.setMaxStudents(request.maxStudents());

        return classSectionRepository.save(classSection);
    }

    @Transactional
    public void deleteClassSection(UUID id) {
        ClassSection classSection = getClassSectionById(id);
        classSectionRepository.delete(classSection);
    }
}