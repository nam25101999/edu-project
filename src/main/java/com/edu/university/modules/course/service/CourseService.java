package com.edu.university.modules.course.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.course.dto.CourseDtos.CourseRequest;
import com.edu.university.modules.course.entity.Course;
import com.edu.university.modules.course.mapper.CourseMapper;
import com.edu.university.modules.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service quản lý danh mục môn học.
 * Đã chuẩn hóa lỗi theo Enterprise Standard (ErrorCode CRS).
 */
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @LogAction(action = "VIEW_ALL_COURSES", entityName = "COURSE")
    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @LogAction(action = "VIEW_COURSE", entityName = "COURSE")
    public Course getCourseById(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Không tìm thấy môn học với ID: " + id));
    }

    @LogAction(action = "CREATE_COURSE", entityName = "COURSE")
    @Transactional
    public Course createCourse(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.courseCode())) {
            throw new BusinessException(ErrorCode.COURSE_ALREADY_EXISTS, "Mã môn học đã tồn tại: " + request.courseCode());
        }

        Course prerequisite = null;
        if (request.prerequisiteCourseId() != null) {
            prerequisite = courseRepository.findById(request.prerequisiteCourseId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Không tìm thấy môn tiên quyết"));
        }

        Course course = courseMapper.toEntity(request);
        course.setPrerequisiteCourse(prerequisite);

        return courseRepository.save(course);
    }

    @LogAction(action = "UPDATE_COURSE", entityName = "COURSE")
    @Transactional
    public Course updateCourse(UUID id, CourseRequest request) {
        Course course = getCourseById(id);

        // Kiểm tra trùng mã môn học nếu có thay đổi mã
        if (!course.getCourseCode().equals(request.courseCode()) &&
                courseRepository.existsByCourseCode(request.courseCode())) {
            throw new BusinessException(ErrorCode.COURSE_ALREADY_EXISTS, "Mã môn học đã tồn tại: " + request.courseCode());
        }

        Course prerequisite = null;
        if (request.prerequisiteCourseId() != null) {
            // Tránh việc tự set môn tiên quyết là chính nó
            if (id.equals(request.prerequisiteCourseId())) {
                throw new BusinessException(ErrorCode.INVALID_PREREQUISITE, "Môn tiên quyết không thể là chính môn học này");
            }
            prerequisite = courseRepository.findById(request.prerequisiteCourseId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "Không tìm thấy môn tiên quyết"));
        }

        courseMapper.updateEntityFromDto(request, course);
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