package com.edu.university.modules.graduation.repository;

import com.edu.university.modules.graduation.entity.GraduationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GraduationResultRepository extends JpaRepository<GraduationResult, UUID> {
    List<GraduationResult> findByStudentId(UUID studentId);
}
