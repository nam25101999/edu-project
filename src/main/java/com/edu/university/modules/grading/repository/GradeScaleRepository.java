package com.edu.university.modules.grading.repository;

import com.edu.university.modules.grading.entity.GradeScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeScaleRepository extends JpaRepository<GradeScale, UUID> {
    Optional<GradeScale> findByScaleCode(String scaleCode);
}
