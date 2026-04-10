package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    Optional<Student> findByStudentCode(String studentCode);

    Optional<Student> findByStudentCodeAndIsActiveTrue(String studentCode);

    boolean existsByStudentCode(String studentCode);

    Optional<Student> findFirstByOrderByStudentCodeDesc();

    boolean existsByUser_Email(String email);

    boolean existsByUserId(UUID userId);

    Optional<Student> findByUserId(UUID userId);

    long countByIsActiveTrue();

    long countByIsActiveFalse();

    @Query("""
            SELECT s FROM Student s
            LEFT JOIN s.user u
            WHERE (:search IS NULL OR :search = '' OR
                   LOWER(COALESCE(s.fullName, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(COALESCE(s.firstName, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(COALESCE(s.lastName, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(COALESCE(s.studentCode, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(COALESCE(s.email, '')) LIKE LOWER(CONCAT('%', :search, '%')) OR
                   LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:isActive IS NULL OR s.isActive = :isActive)
              AND (:departmentId IS NULL OR s.department.id = :departmentId)
              AND (:majorId IS NULL OR s.major.id = :majorId)
              AND (:studentClassId IS NULL OR s.studentClass.id = :studentClassId)
            """)
    Page<Student> searchStudents(
            @Param("search") String search,
            @Param("isActive") Boolean isActive,
            @Param("departmentId") UUID departmentId,
            @Param("majorId") UUID majorId,
            @Param("studentClassId") UUID studentClassId,
            Pageable pageable
    );
}
