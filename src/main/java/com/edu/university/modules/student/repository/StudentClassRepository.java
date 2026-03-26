package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.dto.StudentClassDtos.StudentClassResponse;
import com.edu.university.modules.student.entity.StudentClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface StudentClassRepository extends JpaRepository<StudentClass, UUID> {

    boolean existsByClassCode(String classCode);

    // ✅ GET ALL DTO
    @Query("""
        SELECT new com.edu.university.modules.student.dto.StudentClassDtos$StudentClassResponse(
            c.id,
            c.classCode,
            c.name,
            m.id,
            m.name
        )
        FROM StudentClass c
        JOIN c.major m
    """)
    List<StudentClassResponse> findAllDto();

    // ✅ GET BY ID DTO
    @Query("""
        SELECT new com.edu.university.modules.student.dto.StudentClassDtos$StudentClassResponse(
            c.id,
            c.classCode,
            c.name,
            m.id,
            m.name
        )
        FROM StudentClass c
        JOIN c.major m
        WHERE c.id = :id
    """)
    Optional<StudentClassResponse> findByIdDto(@Param("id") UUID id);

    // ✅ SEARCH + PAGINATION DTO
    @Query("""
        SELECT new com.edu.university.modules.student.dto.StudentClassDtos$StudentClassResponse(
            c.id,
            c.classCode,
            c.name,
            m.id,
            m.name
        )
        FROM StudentClass c
        JOIN c.major m
        WHERE (:keyword IS NULL OR :keyword = '' 
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
            OR LOWER(c.classCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    Page<StudentClassResponse> searchStudentClasses(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}