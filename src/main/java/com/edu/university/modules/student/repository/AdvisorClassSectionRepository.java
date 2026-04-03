package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.AdvisorClassSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdvisorClassSectionRepository extends JpaRepository<AdvisorClassSection, UUID> {

    List<AdvisorClassSection> findByAdvisorId(UUID advisorId);

    // Đổi thành số ít: studentClassId
    List<AdvisorClassSection> findByStudentClassId(UUID studentClassId);

    // Đổi thành số ít: studentClassId
    Optional<AdvisorClassSection> findByStudentClassIdAndIsActiveTrue(UUID studentClassId);

    // Đổi thành số ít: studentClassId
    boolean existsByAdvisorIdAndStudentClassId(UUID advisorId, UUID studentClassId);
}