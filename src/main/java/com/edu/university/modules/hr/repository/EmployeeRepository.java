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
    
    Optional<Employee> findByUserId(UUID userId);
    
    java.util.List<Employee> findByDepartmentId(UUID departmentId);

    java.util.Optional<Employee> findByDepartmentIdAndPositions_Code(UUID departmentId, String positionCode);

    java.util.List<Employee> findByDepartmentIdAndPositions_CodeIn(UUID departmentId, java.util.Collection<String> positionCodes);
    
    @org.springframework.data.jpa.repository.Query("SELECT e FROM Employee e " +
            "JOIN e.user u " +
            "JOIN u.roles r " +
            "WHERE r.name = :roleName AND e.department IS NULL")
    org.springframework.data.domain.Page<Employee> findAvailableLecturers(@org.springframework.data.repository.query.Param("roleName") String roleName, org.springframework.data.domain.Pageable pageable);
}
