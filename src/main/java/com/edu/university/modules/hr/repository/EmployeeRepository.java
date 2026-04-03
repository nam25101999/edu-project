package com.edu.university.modules.hr.repository;

import com.edu.university.modules.hr.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmployeeCode(String employeeCode);

    Optional<Employee> findByEmployeeCodeAndIsActiveTrue(String employeeCode);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByUser_Email(String email);

    boolean existsByUserId(UUID userId);
}
