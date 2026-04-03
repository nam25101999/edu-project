package com.edu.university.modules.registration.repository;

import com.edu.university.modules.registration.entity.CourseRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, UUID> {
    List<CourseRegistration> findByStudentId(UUID studentId);
    List<CourseRegistration> findByCourseSectionId(UUID courseSectionId);
    List<CourseRegistration> findByRegistrationPeriodId(UUID registrationPeriodId);
    boolean existsByStudentIdAndCourseSectionId(UUID studentId, UUID courseSectionId);
}
