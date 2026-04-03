package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentStatusRepository extends JpaRepository<StudentStatus, UUID> {

    // Tìm danh sách trạng thái thông qua ID của object student
    List<StudentStatus> findByStudentId(UUID studentId);

    // Tìm danh sách trạng thái đang active thông qua ID của object student
    List<StudentStatus> findByStudentIdAndIsActiveTrue(UUID studentId);

    // SỬA LỖI Ở ĐÂY: Thay s.effectiveDate bằng s.startDate để khớp với Entity
    @Query("SELECT s FROM StudentStatus s WHERE s.student.id = :studentId AND s.isActive = true ORDER BY s.startDate DESC LIMIT 1")
    Optional<StudentStatus> findLatestActiveStatusByStudentId(@Param("studentId") UUID studentId);
}