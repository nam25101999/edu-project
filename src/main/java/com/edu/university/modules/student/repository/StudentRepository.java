package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByUserId(UUID userId);

    // 🔥 Thêm hàm này để lấy Student kèm luôn dữ liệu User và Major trong 1 lần truy vấn
    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.major m LEFT JOIN FETCH m.faculty")
    List<Student> findAllWithDetails();

    @Query("SELECT s FROM Student s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:majorId IS NULL OR s.major.id = :majorId)")
    Page<Student> searchAndFilterStudents(
            @Param("keyword") String keyword,
            @Param("majorId") UUID majorId,
            Pageable pageable
    );
    Page<Student> findByMajorId(UUID majorId, Pageable pageable);
    // Lấy theo khoa (Truy vấn qua quan hệ Major -> Faculty)
    Page<Student> findByMajorFacultyId(UUID facultyId, Pageable pageable);
    boolean existsByStudentCode(String studentCode);
    long countByMajorId(UUID majorId);

    long countByMajorFacultyId(UUID facultyId);
}