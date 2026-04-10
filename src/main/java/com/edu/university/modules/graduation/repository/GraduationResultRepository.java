package com.edu.university.modules.graduation.repository;

import com.edu.university.modules.graduation.entity.GraduationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GraduationResultRepository extends JpaRepository<GraduationResult, UUID> {
    Page<GraduationResult> findByStudentId(UUID studentId, Pageable pageable);
}
