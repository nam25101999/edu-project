package com.edu.university.modules.grading.repository;

import com.edu.university.modules.grading.entity.GradeComponent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GradeComponentRepository extends JpaRepository<GradeComponent, UUID> {
    Page<GradeComponent> findByCourseSectionId(UUID courseSectionId, Pageable pageable);
}
