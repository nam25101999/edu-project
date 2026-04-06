package com.edu.university.modules.finance.repository;

import com.edu.university.modules.finance.entity.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, UUID> {
    List<Scholarship> findByStudentId(UUID studentId);
    List<Scholarship> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId);
}
