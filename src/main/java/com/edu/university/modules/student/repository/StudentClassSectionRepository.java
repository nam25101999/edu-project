package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.StudentClassSection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentClassSectionRepository extends JpaRepository<StudentClassSection, UUID> {

    Page<StudentClassSection> findByStudentId(UUID studentId, Pageable pageable);

    Page<StudentClassSection> findByStudentClassId(UUID studentClassId, Pageable pageable);

    Page<StudentClassSection> findByStudentClassIdAndIsActiveTrue(UUID studentClassId, Pageable pageable);

    boolean existsByStudentIdAndStudentClassId(UUID studentId, UUID studentClassId);
}