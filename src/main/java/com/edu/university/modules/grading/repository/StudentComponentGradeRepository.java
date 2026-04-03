package com.edu.university.modules.grading.repository;

import com.edu.university.modules.grading.entity.StudentComponentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentComponentGradeRepository extends JpaRepository<StudentComponentGrade, UUID> {
    List<StudentComponentGrade> findByCourseRegistrationId(UUID registrationId);
    List<StudentComponentGrade> findByGradeComponentId(UUID componentId);
}
