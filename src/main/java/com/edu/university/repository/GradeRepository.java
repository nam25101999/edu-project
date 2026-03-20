package com.edu.university.repository;

import com.edu.university.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {
    Optional<Grade> findByEnrollmentId(UUID enrollmentId);
    List<Grade> findByEnrollmentStudentId(UUID studentId);
}