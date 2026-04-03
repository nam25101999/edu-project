package com.edu.university.modules.hr.repository;

import com.edu.university.modules.hr.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    Optional<Department> findByCode(String code);

    Optional<Department> findByCodeAndIsActiveTrue(String code);

    boolean existsByCode(String code);
}
