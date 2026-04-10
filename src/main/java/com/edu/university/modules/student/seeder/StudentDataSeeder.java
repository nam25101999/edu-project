package com.edu.university.modules.student.seeder;

import com.edu.university.common.seeder.ModuleSeeder;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.entity.StudentClass;
import com.edu.university.modules.student.repository.StudentClassRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentDataSeeder implements ModuleSeeder {

    private final StudentRepository studentRepository;
    private final StudentClassRepository studentClassRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AcademicYearRepository academicYearRepository;
    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public void seed() {
        log.info("Seeding Student data...");
        if (studentRepository.count() > 0) return;

        List<AcademicYear> years = academicYearRepository.findAll();
        List<Department> depts = departmentRepository.findAll();
        List<Major> majors = majorRepository.findAll();
        List<TrainingProgram> programs = trainingProgramRepository.findAll();
        List<Employee> employees = employeeRepository.findAll();

        if (years.isEmpty() || depts.isEmpty() || majors.isEmpty() || programs.isEmpty() || employees.isEmpty()) {
            log.warn("Cannot seed students: Missing required dependencies (Years, Depts, Majors, Programs, or Employees).");
            return;
        }

        seedStudentClasses(majors, depts, employees, years.get(0));
        seedStudents(years, depts, majors, programs);
    }

    @Override
    public int getOrder() {
        return 50;
    }

    private void seedStudentClasses(List<Major> majors, List<Department> depts, List<Employee> employees, AcademicYear year) {
        if (studentClassRepository.count() > 0) return;
        List<StudentClass> classes = new ArrayList<>();
        for (int i = 0; i < majors.size(); i++) {
            Major major = majors.get(i);
            Department dept = major.getDepartment();
            for (int j = 1; j <= 2; j++) {
                classes.add(StudentClass.builder()
                        .classCode(major.getMajorCode() + "-C" + j)
                        .className("Lớp " + major.getName() + " " + j)
                        .department(dept)
                        .major(major)
                        .academicYear(year)
                        .advisor(employees.get((i * 2 + j) % employees.size()))
                        .isActive(true)
                        .build());
            }
        }
        studentClassRepository.saveAll(classes);
    }

    private void seedStudents(List<AcademicYear> years, List<Department> depts, List<Major> majors, List<TrainingProgram> programs) {
        if (studentRepository.count() >= 150) return;
        Role studentRole = roleRepository.findByName("STUDENT").orElse(null);
        List<StudentClass> allClasses = studentClassRepository.findAll();
        if (allClasses.isEmpty()) return;

        List<Student> students = new ArrayList<>();
        
        String[] firstNames = {"Nguyễn", "Trân", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý", "Đào", "Đoàn", "Trịnh", "Lâm", "Mai", "Võ", "Phan"};
        String[] middleNames = {"Anh", "Thanh", "Văn", "Thị", "Quốc", "Minh", "Thu", "Hải", "Bích", "Tấn", "Xuân", "Mỹ", "Bảo", "Khánh", "Duy"};
        String[] lastNames = {"Tuấn", "Hà", "Việt", "Lan", "Thái", "Trúc", "Ngọc", "Phát", "Nhi", "Minh", "Quỳnh", "Hậu", "Thanh", "Dung", "Phúc", "Tùng", "Nhi", "Hùng", "Cường", "Trang"};

        for (int i = 0; i < 200; i++) {
            String firstName = firstNames[i % firstNames.length];
            String middleName = middleNames[(i / 2) % middleNames.length];
            String lastName = lastNames[(i / 3) % lastNames.length];
            String fullName = firstName + " " + middleName + " " + lastName;
            
            String stuCode = String.format("%06d", i + 1);
            String username = stuCode.toLowerCase();
            
            if (userRepository.findByUsername(username).isEmpty()) {
                Users user = Users.builder()
                        .username(username)
                        .password(passwordEncoder.encode("123456"))
                        .email(username + "@student.edu.vn")
                        .roles(new java.util.HashSet<>(java.util.Collections.singletonList(studentRole)))
                        .isActive(true)
                        .build();
                userRepository.save(user);
                
                int mIndex = i % majors.size();
                Major major = majors.get(mIndex);
                
                // Find classes for this major
                List<StudentClass> majorClasses = allClasses.stream()
                        .filter(c -> c.getMajor().getId().equals(major.getId()))
                        .toList();
                StudentClass studentClass = majorClasses.isEmpty() ? allClasses.get(0) : majorClasses.get(i % majorClasses.size());

                students.add(Student.builder()
                        .studentCode(stuCode)
                        .fullName(fullName)
                        .firstName(firstName)
                        .lastName(middleName + " " + lastName)
                        .gender(i % 2 == 0 ? "1" : "2")
                        .user(user)
                        .academicYear(years.get(0))
                        .department(major.getDepartment())
                        .major(major)
                        .studentClass(studentClass)
                        .trainingProgram(programs.get(mIndex % programs.size()))
                        .status("STUDYING")
                        .isActive(true)
                        .build());
            }
        }
        studentRepository.saveAll(students);
    }
}
