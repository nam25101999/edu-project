package com.edu.university.modules.elearning.repository;

import com.edu.university.modules.elearning.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {
    List<Material> findByCourseSectionId(UUID courseSectionId);
}
