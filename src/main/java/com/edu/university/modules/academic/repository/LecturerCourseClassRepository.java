package com.edu.university.modules.academic.repository;

import com.edu.university.modules.academic.entity.LecturerCourseClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LecturerCourseClassRepository extends JpaRepository<LecturerCourseClass, UUID> {
    List<LecturerCourseClass> findByCourseSectionId(UUID courseSectionId);
    List<LecturerCourseClass> findByLecturerId(UUID lecturerId);
}
