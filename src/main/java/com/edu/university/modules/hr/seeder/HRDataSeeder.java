package com.edu.university.modules.hr.seeder;

import com.edu.university.common.seeder.ModuleSeeder;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.hr.entity.Position;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import com.edu.university.modules.hr.repository.FacultyRepository;
import com.edu.university.modules.hr.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class HRDataSeeder implements ModuleSeeder {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final FacultyRepository facultyRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void seed() {
        log.info("Seeding HR data...");
        if (facultyRepository.count() > 0) return;

        List<Department> depts = seedDepartments();
        List<Position> positions = seedPositions(depts);
        List<Employee> employees = seedEmployees(depts, positions);
        seedFaculties();
        
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Role lecturerRole = roleRepository.findByName("LECTURER").orElse(null);
        
        if (adminRole != null && lecturerRole != null) {
            linkEmployeesToUsers(employees, adminRole, lecturerRole);
        }
    }

    @Override
    public int getOrder() {
        return 20;
    }

    private List<Department> seedDepartments() {
        if (departmentRepository.count() > 0) return departmentRepository.findAll();
        List<Department> depts = List.of(
                Department.builder().code("CNTT").name("Công nghệ thông tin").establishedDate(LocalDate.of(2000, 1, 1)).build(),
                Department.builder().code("KTE").name("Kinh tế").establishedDate(LocalDate.of(2005, 5, 20)).build(),
                Department.builder().code("NNGU").name("Ngoại ngữ").establishedDate(LocalDate.of(2010, 8, 15)).build(),
                Department.builder().code("CKDT").name("Cơ khí Điện tử").establishedDate(LocalDate.of(2008, 9, 5)).build(),
                Department.builder().code("KHCB").name("Khoa học Cơ bản").establishedDate(LocalDate.of(1998, 11, 20)).build(),
                Department.builder().code("LUAT").name("Luật").establishedDate(LocalDate.of(2012, 3, 10)).build(),
                Department.builder().code("DUOC").name("Dược học").establishedDate(LocalDate.of(2015, 6, 25)).build(),
                Department.builder().code("QUAN").name("Quản trị kinh doanh").establishedDate(LocalDate.of(2007, 7, 7)).build(),
                Department.builder().code("MTHT").name("Môi trường").establishedDate(LocalDate.of(2013, 2, 14)).build(),
                Department.builder().code("XAYD").name("Xây dựng").establishedDate(LocalDate.of(2009, 10, 30)).build());
        return departmentRepository.saveAll(depts);
    }

    private List<Position> seedPositions(List<Department> depts) {
        if (positionRepository.count() >= 10) return positionRepository.findAll();
        List<Position> pos = List.of(
                Position.builder().code("DEAN").name("Trưởng khoa").level("Management").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("VDEAN").name("Phó trưởng khoa").level("Management").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("HOD").name("Trưởng bộ môn").level("Management").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("PROF").name("Giáo sư").level("Academic").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("ASSOC_PROF").name("Phó Giáo sư").level("Academic").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("LEC").name("Giảng viên").level("Academic").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("ASST").name("Trợ giảng").level("Academic").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("ADMIN_H").name("Trưởng phòng hành chính").level("Staff").department(depts.get(1)).isActive(true).build(),
                Position.builder().code("STAFF").name("Nhân viên").level("Staff").department(depts.get(1)).isActive(true).build(),
                Position.builder().code("TECH").name("Kỹ thuật viên").level("Staff").department(depts.get(0)).isActive(true).build());
        return positionRepository.saveAll(pos);
    }

    private List<Employee> seedEmployees(List<Department> depts, List<Position> pos) {
        if (employeeRepository.count() >= 40) return employeeRepository.findAll();
        List<Employee> emps = new ArrayList<>();
        String[] empNames = { 
            "Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Văn D", "Hoàng Thị E", 
            "Vũ Văn F", "Đặng Thị G", "Bùi Văn H", "Đỗ Thị I", "Hồ Văn K", 
            "Ngô Thị L", "Dương Văn M", "Lý Thị N", "Đào Văn O", "Đoàn Thị P",
            "Trịnh Văn Q", "Lâm Thị R", "Mai Văn S", "Phùng Thị T", "Châu Văn U",
            "Đinh Văn V", "Quách Thị W", "Âu Văn X", "Thân Thị Y", "Vi Văn Z",
            "Tạ Văn An", "Uông Thị Bình", "Khổng Văn Cường", "Tôn Thị Đào", "Bạch Văn Em",
            "Chu Văn Giang", "Hà Thị Hoa", "Lương Văn Hùng", "Ninh Thị Lan", "Thái Văn Minh",
            "Bế Thị Nga", "Nông Văn Phúc", "Sầm Thị Quế", "Vương Văn Sơn", "Lục Thị Tuyết"
        };
        for (int i = 0; i < empNames.length; i++) {
            emps.add(Employee.builder()
                    .employeeCode(String.format("NV%03d", i + 1))
                    .fullName(empNames[i])
                    .email(String.format("nv%03d@edu.vn", i + 1))
                    .phone("09" + String.format("%08d", 80000000 + i))
                    .department(depts.get(i % depts.size()))
                    .positions(java.util.Set.of(pos.get(i % pos.size())))
                    .isActive(true)
                    .build());
        }
        return employeeRepository.saveAll(emps);
    }

    private void seedFaculties() {
        List<Faculty> faculties = List.of(
                Faculty.builder().code("CNTT").name("Công nghệ thông tin").establishedYear(LocalDate.of(2000, 1, 1)).build(),
                Faculty.builder().code("KTE").name("Kinh tế").establishedYear(LocalDate.of(2005, 5, 20)).build(),
                Faculty.builder().code("NNGU").name("Ngoại ngữ").establishedYear(LocalDate.of(2010, 8, 15)).build(),
                Faculty.builder().code("CKDT").name("Cơ khí - Điện tử").establishedYear(LocalDate.of(2008, 9, 5)).build(),
                Faculty.builder().code("KHCB").name("Khoa học Cơ bản").establishedYear(LocalDate.of(1998, 11, 20)).build(),
                Faculty.builder().code("LUAT").name("Luật").establishedYear(LocalDate.of(2012, 3, 10)).build(),
                Faculty.builder().code("DUOC").name("Dược học").establishedYear(LocalDate.of(2015, 6, 25)).build(),
                Faculty.builder().code("QUAN").name("Quản trị kinh doanh").establishedYear(LocalDate.of(2007, 7, 7)).build(),
                Faculty.builder().code("MTHT").name("Môi trường").establishedYear(LocalDate.of(2013, 2, 14)).build(),
                Faculty.builder().code("XAYD").name("Xây dựng").establishedYear(LocalDate.of(2009, 10, 30)).build());
        facultyRepository.saveAll(faculties);
    }

    private void linkEmployeesToUsers(List<Employee> employees, Role adminRole, Role lecturerRole) {
        for (Employee emp : employees) {
            if (emp.getUser() == null) {
                // Use employee code as username (normalize to lowercase)
                String username = emp.getEmployeeCode().toLowerCase();
                
                if (userRepository.findByUsername(username).isEmpty()) {
                    // Map roles: Managers receive ADMIN, others receive LECTURER
                    Role targetRole = lecturerRole;
                    if (emp.getPositions() != null && !emp.getPositions().isEmpty()) {
                        boolean isManager = emp.getPositions().stream()
                                .anyMatch(p -> List.of("DEAN", "VDEAN", "HOD", "ADMIN_H").contains(p.getCode()));
                        if (isManager) {
                            targetRole = adminRole;
                        }
                    }

                    Users user = Users.builder()
                            .username(username)
                            .password(passwordEncoder.encode("123456"))
                            .fullName(emp.getFullName())
                            .email(emp.getEmail())
                            .roles(Set.of(targetRole))
                            .isActive(true)
                            .emailVerified(true)
                            .build();
                    userRepository.save(user);
                    emp.setUser(user);
                    employeeRepository.save(emp);
                }
            }
        }
    }
}
