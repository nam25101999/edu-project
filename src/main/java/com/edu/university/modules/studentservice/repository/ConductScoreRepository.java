package com.edu.university.modules.studentservice.repository;

import com.edu.university.modules.studentservice.entity.ConductScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConductScoreRepository extends JpaRepository<ConductScore, UUID> {
    Optional<ConductScore> findByStudentIdAndSemesterId(UUID studentId, UUID semesterId);
    List<ConductScore> findByStudentId(UUID studentId);
}
