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
 * Repository quáº£n lÃ½ thá»±c thá»ƒ User.
 * Bá»• sung phÆ°Æ¡ng thá»©c countByRolesContaining Ä‘á»ƒ phá»¥c vá»¥ thá»‘ng kÃª trong ReportService.
 */
@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    /**
     * ÄÄƒng nháº­p linh hoáº¡t: TÃ¬m User báº±ng username, email hoáº·c mÃ£ sá»‘ sinh viÃªn.
     */
    @Query("SELECT u FROM Users u " +
            "LEFT JOIN Student s ON s.user.id = u.id " +
            "WHERE u.username = :identifier OR u.email = :identifier OR s.studentCode = :identifier")
    Optional<Users> findByIdentifier(@Param("identifier") String identifier);

    /**
     * Äáº¿m sá»‘ lÆ°á»£ng ngÆ°á»i dÃ¹ng theo vai trÃ² (Role).
     * @param role Vai trÃ² cáº§n Ä‘áº¿m (ADMIN, STUDENT, LECTURER...)
     * @return Sá»‘ lÆ°á»£ng ngÆ°á»i dÃ¹ng thuá»™c vai trÃ² Ä‘Ã³.
     */
    long countByRolesContaining(Role role); // ÄÃƒ Sá»¬A: Äá»•i countByRole thÃ nh countByRolesContaining
}
