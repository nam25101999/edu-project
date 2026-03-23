package com.edu.university.modules.auth.repository;

import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository quản lý thực thể User.
 * Bổ sung phương thức countByRole để phục vụ thống kê trong ReportService.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    /**
     * Đếm số lượng người dùng theo vai trò (Role).
     * @param role Vai trò cần đếm (ADMIN, STUDENT, LECTURER...)
     * @return Số lượng người dùng thuộc vai trò đó.
     */
    long countByRole(Role role);
}