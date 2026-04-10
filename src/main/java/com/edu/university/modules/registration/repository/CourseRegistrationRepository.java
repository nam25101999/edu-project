package com.edu.university.modules.registration.repository;

import com.edu.university.modules.registration.entity.CourseRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRegistrationRepository extends JpaRepository<CourseRegistration, UUID> {
    Page<CourseRegistration> findByStudentId(UUID studentId, Pageable pageable);
    List<CourseRegistration> findByStudentId(UUID studentId);
    
    Page<CourseRegistration> findByCourseSectionId(UUID courseSectionId, Pageable pageable);
    List<CourseRegistration> findByCourseSectionId(UUID courseSectionId);
    List<CourseRegistration> findByCourseSectionIdAndStatus(UUID courseSectionId, Integer status);
    
    Page<CourseRegistration> findByRegistrationPeriodId(UUID registrationPeriodId, Pageable pageable);
    List<CourseRegistration> findByRegistrationPeriodId(UUID registrationPeriodId);
    
    boolean existsByStudentIdAndCourseSectionId(UUID studentId, UUID courseSectionId);
}
