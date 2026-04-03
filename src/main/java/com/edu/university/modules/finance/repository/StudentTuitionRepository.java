package com.edu.university.modules.finance.repository;

import com.edu.university.modules.finance.entity.StudentTuition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentTuitionRepository extends JpaRepository<StudentTuition, UUID> {
    List<StudentTuition> findByStudentId(UUID studentId);
    List<StudentTuition> findBySemesterId(UUID semesterId);
}
