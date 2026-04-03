package com.edu.university.modules.registration.repository;

import com.edu.university.modules.registration.entity.EquivalentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquivalentCourseRepository extends JpaRepository<EquivalentCourse, UUID> {
    List<EquivalentCourse> findByOriginalCourseId(UUID originalCourseId);
    List<EquivalentCourse> findByEquivalentCourseId(UUID equivalentCourseId);
}
