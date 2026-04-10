package com.edu.university.modules.elearning.repository;

import com.edu.university.modules.elearning.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    Page<Assignment> findByCourseSectionId(UUID courseSectionId, Pageable pageable);
}
