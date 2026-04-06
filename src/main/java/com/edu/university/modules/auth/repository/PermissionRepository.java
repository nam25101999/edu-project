package com.edu.university.modules.auth.repository;

import com.edu.university.modules.auth.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository quáº£n lÃ½ thá»±c thá»ƒ Permission (Quyá»n háº¡n).
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    // TÃ¬m kiáº¿m quyá»n theo tÃªn chÃ­nh xÃ¡c (VD: "USER_CREATE")
    Optional<Permission> findByName(String name);

    // TÃ¬m kiáº¿m quyá»n theo TÃ i nguyÃªn (Resource) vÃ  HÃ nh Ä‘á»™ng (Action)
    Optional<Permission> findByResourceAndAction(String resource, String action);

    // Láº¥y toÃ n bá»™ danh sÃ¡ch quyá»n thuá»™c vá» má»™t TÃ i nguyÃªn cá»¥ thá»ƒ (VD: "STUDENT")
    List<Permission> findByResource(String resource);

    // Láº¥y danh sÃ¡ch cÃ¡c quyá»n Ä‘ang Ä‘Æ°á»£c kÃ­ch hoáº¡t
    List<Permission> findByIsActiveTrue();

    // Kiá»ƒm tra xem má»™t quyá»n Ä‘Ã£ tá»“n táº¡i theo tÃªn chÆ°a
    boolean existsByName(String name);
}
