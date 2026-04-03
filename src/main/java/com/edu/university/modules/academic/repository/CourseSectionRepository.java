package com.edu.university.modules.academic.repository;

import com.edu.university.modules.academic.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID> {
    Optional<CourseSection> findByClassCode(String classCode);
    boolean existsByClassCode(String classCode);
}
