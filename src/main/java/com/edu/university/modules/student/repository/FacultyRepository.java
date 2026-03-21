package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FacultyRepository extends JpaRepository<Faculty, UUID> {

    boolean existsByFacultyCode(String facultyCode);

    Optional<Faculty> findByFacultyCode(String facultyCode);

}