package com.edu.university.service;

import com.edu.university.annotation.LogAction;
import com.edu.university.dto.CourseDtos.CourseRequest;
import com.edu.university.entity.Course;
import com.edu.university.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @LogAction(action = "VIEW_ALL_COURSES", entityName = "COURSE")
    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @LogAction(action = "VIEW_COURSE", entityName = "COURSE")
    public Course getCourseById(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID: " + id));
    }

    @LogAction(action = "CREATE_COURSE", entityName = "COURSE")
    @Transactional
    public Course createCourse(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.courseCode())) {
            throw new RuntimeException("Mã môn học đã tồn tại: " + request.courseCode());
        }

        Course prerequisite = null;
        if (request.prerequisiteCourseId() != null) {
            prerequisite = courseRepository.findById(request.prerequisiteCourseId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy môn tiên quyết với ID: " + request.prerequisiteCourseId()));
        }

        Course course = Course.builder()
                .courseCode(request.courseCode())
                .name(request.name())
                .credits(request.credits())
                .prerequisiteCourse(prerequisite)
                .build();

        return courseRepository.save(course);
    }

    @LogAction(action = "UPDATE_COURSE", entityName = "COURSE")
    @Transactional
    public Course updateCourse(UUID id, CourseRequest request) {
        Course course = getCourseById(id);

        // Kiểm tra trùng mã môn học nếu có thay đổi mã
        if (!course.getCourseCode().equals(request.courseCode()) &&
                courseRepository.existsByCourseCode(request.courseCode())) {
            throw new RuntimeException("Mã môn học đã tồn tại: " + request.courseCode());
        }

        Course prerequisite = null;
        if (request.prerequisiteCourseId() != null) {
            // Tránh việc tự set môn tiên quyết là chính nó
            if (id.equals(request.prerequisiteCourseId())) {
                throw new RuntimeException("Môn tiên quyết không thể là chính môn học này");
            }
            prerequisite = courseRepository.findById(request.prerequisiteCourseId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy môn tiên quyết"));
        }

        course.setCourseCode(request.courseCode());
        course.setName(request.name());
        course.setCredits(request.credits());
        course.setPrerequisiteCourse(prerequisite);

        return courseRepository.save(course);
    }

    @LogAction(action = "DELETE_COURSE", entityName = "COURSE")
    @Transactional
    public void deleteCourse(UUID id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }
}