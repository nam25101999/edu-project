package com.edu.university.modules.studentservice.repository;

import com.edu.university.modules.studentservice.entity.StudentPetition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentPetitionRepository extends JpaRepository<StudentPetition, UUID> {
    Page<StudentPetition> findByStudentId(UUID studentId, Pageable pageable);
    Page<StudentPetition> findByStatus(String status, Pageable pageable);
}
