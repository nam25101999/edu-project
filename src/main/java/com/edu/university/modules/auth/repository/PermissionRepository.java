package com.edu.university.modules.auth.repository;

import com.edu.university.modules.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository quản lý thực thể Permission (Quyền hạn).
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // Tìm kiếm quyền theo tên chính xác (VD: "USER_CREATE")
    Optional<Permission> findByName(String name);

    // Tìm kiếm quyền theo Tài nguyên (Resource) và Hành động (Action)
    Optional<Permission> findByResourceAndAction(String resource, String action);

    // Lấy toàn bộ danh sách quyền thuộc về một Tài nguyên cụ thể (VD: "STUDENT")
    List<Permission> findByResource(String resource);

    // Lấy danh sách các quyền đang được kích hoạt
    List<Permission> findByIsActiveTrue();

    // Kiểm tra xem một quyền đã tồn tại theo tên chưa
    boolean existsByName(String name);
}