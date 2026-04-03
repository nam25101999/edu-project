package com.edu.university.modules.grading.repository;

import com.edu.university.modules.grading.entity.GradeComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GradeComponentRepository extends JpaRepository<GradeComponent, UUID> {
    List<GradeComponent> findByCourseSectionId(UUID courseSectionId);
}
