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
        
        Role lecturerRole = roleRepository.findByName("LECTURER").orElse(null);
        if (lecturerRole != null) {
            linkLecturersToEmployees(employees, lecturerRole);
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
        if (positionRepository.count() > 0) return positionRepository.findAll();
        List<Position> pos = List.of(
                Position.builder().code("TK").name("Trưởng khoa").level("Management").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("GV").name("Giảng viên").level("Academic").department(depts.get(0)).isActive(true).build(),
                Position.builder().code("NV").name("Nhân viên").level("Staff").department(depts.get(1)).isActive(true).build());
        return positionRepository.saveAll(pos);
    }

    private List<Employee> seedEmployees(List<Department> depts, List<Position> pos) {
        if (employeeRepository.count() > 0) return employeeRepository.findAll();
        List<Employee> emps = new ArrayList<>();
        String[] empNames = { "Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Văn D", "Hoàng Thị E", "Vũ Văn F", "Đặng Thị G", "Bùi Văn H", "Đỗ Thị I", "Hồ Văn K", "Ngô Thị L", "Dương Văn M", "Lý Thị N", "Đào Văn O", "Đoàn Thị P" };
        for (int i = 0; i < empNames.length; i++) {
            emps.add(Employee.builder()
                    .employeeCode(String.format("NV%03d", i + 1))
                    .fullName(empNames[i])
                    .email(String.format("nv%03d@edu.vn", i + 1))
                    .phone("09" + (80000000 + i))
                    .department(depts.get(i % depts.size()))
                    .position(i % 5 == 0 ? pos.get(0) : (i % 2 == 0 ? pos.get(1) : pos.get(2)))
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

    private void linkLecturersToEmployees(List<Employee> employees, Role lecturerRole) {
        for (Employee emp : employees) {
            if (emp.getPosition() != null && "Academic".equals(emp.getPosition().getLevel()) && emp.getUser() == null) {
                String username = "gv_" + emp.getEmployeeCode().toLowerCase();
                if (userRepository.findByUsername(username).isEmpty()) {
                    Users user = Users.builder()
                            .username(username)
                            .password(passwordEncoder.encode("123456"))
                            .email(emp.getEmail())
                            .roles(Set.of(lecturerRole))
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
