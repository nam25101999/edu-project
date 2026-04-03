package com.edu.university.modules.student.repository;

import com.edu.university.modules.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    // Tìm sinh viên theo mã sinh viên
    Optional<Student> findByStudentCode(String studentCode);

    // Tìm sinh viên theo mã và chưa bị xóa mềm / đang hoạt động
    Optional<Student> findByStudentCodeAndIsActiveTrue(String studentCode);

    // Kiểm tra mã sinh viên đã tồn tại chưa (Dùng khi Create/Update)
    boolean existsByStudentCode(String studentCode);

    // SỬA LỖI Ở ĐÂY: Truy cập vào trường email của bảng Users thông qua thuộc tính user
    boolean existsByUser_Email(String email);

    // Kiểm tra user_id đã được liên kết với sinh viên nào chưa
    boolean existsByUserId(UUID userId);

    // Tìm kiếm thông tin sinh viên theo user_id quản lý
    Optional<Student> findByUserId(UUID userId);
}