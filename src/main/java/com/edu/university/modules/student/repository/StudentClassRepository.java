package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StudentClassRepository extends JpaRepository<StudentClass, UUID> {
    boolean existsByClassCode(String classCode);
}