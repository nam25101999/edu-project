package com.edu.university.modules.graduation.repository;

import com.edu.university.modules.graduation.entity.GraduationCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GraduationConditionRepository extends JpaRepository<GraduationCondition, UUID> {
    List<GraduationCondition> findByTrainingProgramId(UUID trainingProgramId);
}
