package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByUserId(UUID userId);
    boolean existsByStudentCode(String studentCode);
}