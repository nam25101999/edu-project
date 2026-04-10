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
        if (permissionRepository.count() >= 15) return;

        List<Permission> permissions = List.of(
                createPerm("STUDENT", "CREATE"), createPerm("STUDENT", "VIEW"),
                createPerm("STUDENT", "UPDATE"), createPerm("STUDENT", "DELETE"),
                createPerm("STUDENT", "IMPORT"), createPerm("STUDENT", "EXPORT"),
                createPerm("ACADEMIC", "VIEW"), createPerm("ACADEMIC", "MANAGE"),
                createPerm("CURRICULUM", "VIEW"), createPerm("CURRICULUM", "MANAGE"),
                createPerm("HR", "VIEW"), createPerm("HR", "MANAGE"),
                createPerm("FINANCE", "VIEW"), createPerm("FINANCE", "MANAGE"),
                createPerm("REGISTRATION", "VIEW"), createPerm("REGISTRATION", "PROCESS"),
                createPerm("GRADE", "VIEW"), createPerm("GRADE", "UPDATE"),
                createPerm("SYSTEM", "MANAGE"));
        permissionRepository.saveAll(permissions);
    }

    private Permission createPerm(String resource, String action) {
        return Permission.builder().resource(resource).action(action)
                .description(resource + "_" + action).isActive(true).build();
    }

    private void seedRoles() {
        if (roleRepository.count() >= 3) return;

        List<Permission> all = permissionRepository.findAll();
        Role admin = Role.builder().name("ADMIN").description("Quản trị hệ thống (Toàn quyền)")
                .permissions(new HashSet<>(all)).build();
        
        // Consolidated Staff Role (Lecturer + Former Dean/Registrar/Accountant)
        Role lecturer = Role.builder().name("LECTURER").description("Nhân sự/Giảng viên (Quyền nghiệp vụ)")
                .permissions(new HashSet<>(filterPerms(all, "STUDENT", "ACADEMIC", "CURRICULUM", "HR", "FINANCE", "REGISTRATION", "GRADE"))).build();
        
        Role student = Role.builder().name("STUDENT").description("Sinh viên (Truy cập hạn chế)")
                .permissions(new HashSet<>(filterPerms(all, "STUDENT_VIEW", "GRADE_VIEW", "ACADEMIC_VIEW"))).build();

        roleRepository.saveAll(List.of(admin, lecturer, student));
    }

    private List<Permission> filterPerms(List<Permission> all, String... resources) {
        Set<String> resourceSet = Set.of(resources);
        return all.stream()
                .filter(p -> resourceSet.stream().anyMatch(r -> p.getName().contains(r)))
                .toList();
    }

    private void seedAdmin(Role adminRole) {
        if (userRepository.findByUsername("admin").isPresent()) return;
        Users admin = Users.builder()
                .username("admin")
                .password(passwordEncoder.encode("123456"))
                .fullName("System Administrator")
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
                .fullName("Nguyễn Nam")
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
                .fullName("Student One")
                .email("student01@university.edu.vn")
                .roles(Set.of(studentRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(student);
        
        Users student2 = Users.builder()
                .username("student02")
                .password(passwordEncoder.encode("123456"))
                .fullName("Student Two")
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
                .fullName("Lecturer One")
                .email("lecturer01@university.edu.vn")
                .roles(Set.of(lecturerRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(lecturer1);

        Users lecturer2 = Users.builder()
                .username("lecturer02")
                .password(passwordEncoder.encode("123456"))
                .fullName("Lecturer Two")
                .email("lecturer02@university.edu.vn")
                .roles(Set.of(lecturerRole))
                .isActive(true)
                .emailVerified(true)
                .build();
        userRepository.save(lecturer2);
    }
}
