package com.edu.university.modules.academic.repository;

import com.edu.university.modules.academic.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, UUID> {
    Optional<Semester> findBySemesterCode(String semesterCode);
    boolean existsBySemesterCode(String semesterCode);
    Page<Semester> findByAcademicYear_StartDateGreaterThanEqual(LocalDate startDate, Pageable pageable);
    Page<Semester> findByAcademicYear_AcademicYear(String academicYear, Pageable pageable);
}
