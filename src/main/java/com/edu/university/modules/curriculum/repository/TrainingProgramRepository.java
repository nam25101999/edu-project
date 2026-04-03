package com.edu.university.modules.curriculum.repository;

import com.edu.university.modules.curriculum.entity.TrainingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, UUID> {
    Optional<TrainingProgram> findByProgramCode(String programCode);
    boolean existsByProgramCode(String programCode);
}
