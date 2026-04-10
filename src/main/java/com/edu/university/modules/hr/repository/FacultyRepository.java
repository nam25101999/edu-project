package com.edu.university.modules.hr.repository;

import com.edu.university.modules.hr.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, UUID> {
    Optional<Faculty> findByCode(String code);
    Optional<Faculty> findFirstByCode(String code);
}
