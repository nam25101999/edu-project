package com.edu.university.modules.grading.repository;

import com.edu.university.modules.grading.entity.StudentComponentGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentComponentGradeRepository extends JpaRepository<StudentComponentGrade, UUID> {
    Page<StudentComponentGrade> findByCourseRegistrationId(UUID registrationId, Pageable pageable);
    Page<StudentComponentGrade> findByGradeComponentId(UUID componentId, Pageable pageable);
    Optional<StudentComponentGrade> findByCourseRegistrationIdAndGradeComponentId(UUID registrationId, UUID componentId);
}
