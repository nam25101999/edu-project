package com.edu.university.modules.auth.repository;

import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository quản lý thực thể User.
 * Bổ sung phương thức countByRolesContaining để phục vụ thống kê trong ReportService.
 */
@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    /**
     * Đăng nhập linh hoạt: Tìm User bằng username, email hoặc mã số sinh viên.
     */
    @Query("SELECT u FROM Users u " +
            "LEFT JOIN Student s ON s.user.id = u.id " +
            "WHERE u.username = :identifier OR u.email = :identifier OR s.studentCode = :identifier")
    Optional<Users> findByIdentifier(@Param("identifier") String identifier);

    /**
     * Đếm số lượng người dùng theo vai trò (Role).
     * @param role Vai trò cần đếm (ADMIN, STUDENT, LECTURER...)
     * @return Số lượng người dùng thuộc vai trò đó.
     */
    long countByRolesContaining(Role role); // ĐÃ SỬA: Đổi countByRole thành countByRolesContaining
}