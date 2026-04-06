package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.AdvisorClassSection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdvisorClassSectionRepository extends JpaRepository<AdvisorClassSection, UUID> {

    Page<AdvisorClassSection> findByAdvisorId(UUID advisorId, Pageable pageable);

    Page<AdvisorClassSection> findByStudentClassId(UUID studentClassId, Pageable pageable);

    Optional<AdvisorClassSection> findByStudentClassIdAndIsActiveTrue(UUID studentClassId);

    boolean existsByAdvisorIdAndStudentClassId(UUID advisorId, UUID studentClassId);
}