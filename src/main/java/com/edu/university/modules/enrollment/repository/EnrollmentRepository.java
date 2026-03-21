package com.edu.university.modules.enrollment.repository;

import com.edu.university.modules.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    long countByClassSectionId(UUID classSectionId);
    List<Enrollment> findByStudentId(UUID studentId);
    boolean existsByStudentIdAndClassSectionId(UUID studentId, UUID classSectionId);
    List<Enrollment> findByClassSectionId(UUID classSectionId);
}