package com.edu.university.modules.academic.repository;

import com.edu.university.modules.academic.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {
    Optional<AcademicYear> findByAcademicCode(String academicCode);
    Optional<AcademicYear> findByAcademicYear(String academicYear);
    boolean existsByAcademicCode(String academicCode);
}
