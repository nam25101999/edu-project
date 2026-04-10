package com.edu.university.modules.finance.repository;

import com.edu.university.modules.finance.entity.StudentTuition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentTuitionRepository extends JpaRepository<StudentTuition, UUID> {
    Page<StudentTuition> findByStudentId(UUID studentId, Pageable pageable);
    Page<StudentTuition> findBySemesterId(UUID semesterId, Pageable pageable);
}
