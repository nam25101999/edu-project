package com.edu.university.modules.finance.repository;

import com.edu.university.modules.finance.entity.TuitionFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TuitionFeeRepository extends JpaRepository<TuitionFee, UUID> {
    List<TuitionFee> findByTrainingProgramId(UUID trainingProgramId);
}
