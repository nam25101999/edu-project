package com.edu.university.modules.studentservice.repository;

import com.edu.university.modules.studentservice.entity.StudentPetition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentPetitionRepository extends JpaRepository<StudentPetition, UUID> {
    List<StudentPetition> findByStudentId(UUID studentId);
    List<StudentPetition> findByStatus(String status);
}
