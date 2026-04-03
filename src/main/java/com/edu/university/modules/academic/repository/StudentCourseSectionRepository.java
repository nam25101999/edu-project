package com.edu.university.modules.academic.repository;

import com.edu.university.modules.academic.entity.StudentCourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentCourseSectionRepository extends JpaRepository<StudentCourseSection, UUID> {
    List<StudentCourseSection> findByStudentId(UUID studentId);
    List<StudentCourseSection> findByCourseSectionId(UUID courseSectionId);
    Optional<StudentCourseSection> findByStudentIdAndCourseSectionId(UUID studentId, UUID courseSectionId);
}
