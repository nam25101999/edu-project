package com.edu.university.modules.auth.seeder;

import com.edu.university.common.seeder.ModuleSeeder;
import com.edu.university.modules.auth.entity.Permission;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.PermissionRepository;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthDataSeeder implements ModuleSeeder {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void seed() {
        log.info("Seeding Auth data...");
        seedPermissions();
        seedRoles();
        
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role lecturerRole = roleRepository.findByName("LECTURER").orElseThrow();
        Role studentRole = roleRepository.findByName("STUDENT").orElseThrow();

        seedAdmin(adminRole);
        seedAdmin2(adminRole);
        seedStudentAccount(studentRole);
        seedLecturerAccounts(lecturerRole);
    }

    @Override
    public int getOrder() {
        return 10;
    }

    private void seedPermissions() {
        if (permissionRepository.count() > 0) return;

        List<Permission> permissions = List.of(
                createPerm("STUDENT", "CREATE"), createPerm("STUDENT", "VIEW"),
                createPerm("STUDENT", "UPDATE"), createPerm("STUDENT", "DELETE"),
                createPerm("GRADE", "VIEW"), createPerm("GRADE", "UPDATE"),
                createPerm("SYSTEM", "MANAGE"));
        permissionRepository.saveAll(permissions);
    }

    private Permission createPerm(String resource, String action) {
        return Permission.builder().resource(resource).action(action)
                .description(resource + "_" + action).isActive(true).build();
    }

    private void seedRoles() {
        if (roleRepository.count() > 0) return;

        List<Permission> all = permissionRepository.findAll();
        Role admin = Role.builder().name("ADMIN").description("Toàn quyền hệ thống")
                .permissions(new HashSet<>(all)).build();
        Role lecturer = Role.builder().name("LECTURER").description("Giảng viên")
                .permissions(new HashSet<>()).build();
        Role student = Role.builder().name("STUDENT").description("Sinh viên")
                .permissions(new HashSet<>()).build();

        roleRepository.saveAll(List.of(admin, lecturer, student));
    }

    private void seedAdmin(Role adminRole) {
        if (userRepository.findByUsername("admin").isPresent()) return;
        Users admin = Users.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .email("admin@university.edu.vn")
                .roles(Set.of(adminRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(admin);
    }

    private void seedAdmin2(Role adminRole) {
        if (userRepository.findByUsername("nguyennam").isPresent()) return;
        Users admin2 = Users.builder()
                .username("nguyennam")
                .password(passwordEncoder.encode("123456"))
                .email("nguyennam25101999@gmail.com")
                .roles(Set.of(adminRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(admin2);
    }

    private void seedStudentAccount(Role studentRole) {
        if (userRepository.findByUsername("student01").isPresent()) return;

        Users student = Users.builder()
                .username("student01")
                .password(passwordEncoder.encode("123456"))
                .email("student01@university.edu.vn")
                .roles(Set.of(studentRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(student);
        
        Users student2 = Users.builder()
                .username("student02")
                .password(passwordEncoder.encode("123456"))
                .email("student02@university.edu.vn")
                .roles(Set.of(studentRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(student2);
    }

    private void seedLecturerAccounts(Role lecturerRole) {
        if (userRepository.findByUsername("lecturer01").isPresent()) return;

        Users lecturer1 = Users.builder()
                .username("lecturer01")
                .password(passwordEncoder.encode("123456"))
                .email("lecturer01@university.edu.vn")
                .roles(Set.of(lecturerRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(lecturer1);

        Users lecturer2 = Users.builder()
                .username("lecturer02")
                .password(passwordEncoder.encode("123456"))
                .email("lecturer02@university.edu.vn")
                .roles(Set.of(lecturerRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(lecturer2);
    }
}
