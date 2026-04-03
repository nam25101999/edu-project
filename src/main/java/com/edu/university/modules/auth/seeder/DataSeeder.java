package com.edu.university.modules.auth.seeder;

import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.auth.entity.Permission;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.entity.Users;
import com.edu.university.modules.auth.repository.PermissionRepository;
import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.finance.entity.TuitionFee;
import com.edu.university.modules.finance.repository.TuitionFeeRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.entity.Employee;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.hr.entity.Position;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.EmployeeRepository;
import com.edu.university.modules.hr.repository.FacultyRepository;
import com.edu.university.modules.hr.repository.PositionRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.schedule.repository.RoomRepository;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
@org.springframework.context.annotation.Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final FacultyRepository facultyRepository;

    private final AcademicYearRepository academicYearRepository;
    private final SemesterRepository semesterRepository;

    private final MajorRepository majorRepository;
    private final CourseRepository courseRepository;
    private final TrainingProgramRepository trainingProgramRepository;

    private final StudentRepository studentRepository;
    private final TuitionFeeRepository tuitionFeeRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ScheduleRepository scheduleRepository;
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("===== START SEED DATA =====");

        seedPermissions();
        Role adminRole = seedRoles();
        seedAdmin(adminRole);

        List<Department> depts = seedDepartments();
        List<Position> positions = seedPositions(depts);
        seedEmployees(depts, positions);

        List<AcademicYear> years = seedAcademicYears();
        seedSemesters(years);

        List<Faculty> faculties = seedFaculties();
        List<Major> majors = seedMajors(faculties);
        seedCourses(depts);
        List<TrainingProgram> programs = seedTrainingPrograms(majors);

        List<Student> students = seedStudents(years, depts, majors, programs);
        seedTuitionFees(programs);

        List<CourseSection> sections = seedCourseSections(depts, years, majors);
        seedSchedules(sections);
        seedCourseRegistrations(students, sections);

        log.info("===== END SEED DATA =====");
    }

    private void seedPermissions() {
        if (permissionRepository.count() > 0)
            return;
        List<Permission> permissions = List.of(
                createPerm("STUDENT", "CREATE"), createPerm("STUDENT", "VIEW"),
                createPerm("STUDENT", "UPDATE"), createPerm("STUDENT", "DELETE"),
                createPerm("GRADE", "VIEW"), createPerm("GRADE", "UPDATE"),
                createPerm("SYSTEM", "MANAGE"));
        permissionRepository.saveAll(permissions);
    }

    private Permission createPerm(String resource, String action) {
        return Permission.builder().resource(resource).action(action).description(resource + "_" + action)
                .isActive(true).build();
    }

    private Role seedRoles() {
        if (roleRepository.count() > 0)
            return roleRepository.findByName("ADMIN").orElse(null);
        List<Permission> all = permissionRepository.findAll();
        Role admin = Role.builder().name("ADMIN").description("Full quyền").permissions(new HashSet<>(all)).build();
        Role lecturer = Role.builder().name("LECTURER").description("Giảng viên").permissions(new HashSet<>()).build();
        Role student = Role.builder().name("STUDENT").description("Sinh viên").permissions(new HashSet<>()).build();
        roleRepository.saveAll(List.of(admin, lecturer, student));
        return admin;
    }

    private void seedAdmin(Role adminRole) {
        if (userRepository.findByUsername("admin").isPresent())
            return;
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

    private List<Department> seedDepartments() {
        if (departmentRepository.count() > 0)
            return departmentRepository.findAll();
        List<Department> depts = List.of(
                Department.builder().code("CNTT").name("Công nghệ thông tin").establishedYear(LocalDate.of(2000, 1, 1))
                        .build(),
                Department.builder().code("KTE").name("Kinh tế").establishedYear(LocalDate.of(2005, 5, 20)).build(),
                Department.builder().code("NNGU").name("Ngoại ngữ").establishedYear(LocalDate.of(2010, 8, 15)).build());
        return departmentRepository.saveAll(depts);
    }

    private List<Position> seedPositions(List<Department> depts) {
        if (positionRepository.count() > 0)
            return positionRepository.findAll();
        List<Position> pos = List.of(
                Position.builder().code("TK").name("Trưởng khoa").level("Management").department(depts.get(0))
                        .isActive(true).build(),
                Position.builder().code("GV").name("Giảng viên").level("Academic").department(depts.get(0))
                        .isActive(true).build(),
                Position.builder().code("NV").name("Nhân viên").level("Staff").department(depts.get(1)).isActive(true)
                        .build());
        return positionRepository.saveAll(pos);
    }

    private void seedEmployees(List<Department> depts, List<Position> pos) {
        if (employeeRepository.count() > 0)
            return;
        List<Employee> emps = List.of(
                Employee.builder().employeeCode("NV001").fullName("Nguyễn Văn A").email("nva@edu.vn")
                        .phone("0987654321").department(depts.get(0)).position(pos.get(0)).isActive(true).build(),
                Employee.builder().employeeCode("NV002").fullName("Trần Thị B").email("ttb@edu.vn").phone("0123456789")
                        .department(depts.get(0)).position(pos.get(1)).isActive(true).build(),
                Employee.builder().employeeCode("NV003").fullName("Lê Văn C").email("lvc@edu.vn").phone("0909090909")
                        .department(depts.get(1)).position(pos.get(2)).isActive(true).build());
        employeeRepository.saveAll(emps);
    }

    private List<AcademicYear> seedAcademicYears() {
        if (academicYearRepository.count() > 0)
            return academicYearRepository.findAll();
        return academicYearRepository.saveAll(List.of(
                AcademicYear.builder().academicCode("2023-2024").academicName("Năm học 2023-2024")
                        .startDate(LocalDate.of(2023, 9, 1)).endDate(LocalDate.of(2024, 8, 31)).build(),
                AcademicYear.builder().academicCode("2024-2025").academicName("Năm học 2024-2025")
                        .startDate(LocalDate.of(2024, 9, 1)).endDate(LocalDate.of(2025, 8, 31)).build()));
    }

    private void seedSemesters(List<AcademicYear> years) {
        if (semesterRepository.count() > 0)
            return;
        semesterRepository.saveAll(List.of(
                Semester.builder().semesterCode("HK1-23").semesterName("Học kỳ 1").academicYear("2023-2024").build(),
                Semester.builder().semesterCode("HK2-23").semesterName("Học kỳ 2").academicYear("2023-2024").build()));
    }

    private List<Faculty> seedFaculties() {
        if (facultyRepository.count() > 0)
            return facultyRepository.findAll();
        List<Faculty> faculties = List.of(
                Faculty.builder().code("CNTT").name("Công nghệ thông tin").establishedYear(LocalDate.of(2000, 1, 1)).build(),
                Faculty.builder().code("KTE").name("Kinh tế").establishedYear(LocalDate.of(2005, 5, 20)).build(),
                Faculty.builder().code("NNGU").name("Ngoại ngữ").establishedYear(LocalDate.of(2010, 8, 15)).build()
        );
        return facultyRepository.saveAll(faculties);
    }

    private List<Major> seedMajors(List<Faculty> faculties) {
        if (majorRepository.count() > 0)
            return majorRepository.findAll();

        List<Major> majors = List.of(
                Major.builder()
                        .code("CNTT")
                        .name("Công nghệ thông tin")
                        .faculty(faculties.get(0))
                        .effectiveDate("2023-09-01")
                        .expiryDate("2033-08-31")
                        .isActive(true)
                        .build(),

                Major.builder()
                        .code("QTKD")
                        .name("Quản trị kinh doanh")
                        .faculty(faculties.get(1))
                        .effectiveDate("2023-09-01")
                        .expiryDate("2033-08-31")
                        .isActive(true)
                        .build());

        return majorRepository.saveAll(majors);
    }

    private void seedCourses(List<Department> depts) {
        if (courseRepository.count() > 0)
            return;
        courseRepository.saveAll(List.of(
                Course.builder().code("JAVA01").name("Lập trình Java Cơ bản").credits(BigDecimal.valueOf(3))
                        .department(depts.get(0)).isActive(true).build(),
                Course.builder().code("DB01").name("Cơ sở dữ liệu").credits(BigDecimal.valueOf(3))
                        .department(depts.get(0)).isActive(true).build(),
                Course.builder().code("MKT01").name("Marketing căn bản").credits(BigDecimal.valueOf(2))
                        .department(depts.get(1)).isActive(true).build()));
    }

    private List<TrainingProgram> seedTrainingPrograms(List<Major> majors) {
        if (trainingProgramRepository.count() > 0)
            return trainingProgramRepository.findAll();
        return trainingProgramRepository.saveAll(List.of(
                TrainingProgram.builder().programCode("CT-CNTT").programName("Chương trình CNTT Chuẩn")
                        .major(majors.get(0)).build(),
                TrainingProgram.builder().programCode("CT-QTKD").programName("Chương trình QTKD CLC")
                        .major(majors.get(1)).build()));
    }

    private List<Student> seedStudents(List<AcademicYear> years, List<Department> depts, List<Major> majors,
            List<TrainingProgram> programs) {
        if (studentRepository.count() > 0)
            return studentRepository.findAll();

        Role studentRole = roleRepository.findByName("STUDENT").orElse(null);

        // SV1
        Users user1 = Users.builder()
                .username("sv001")
                .password(passwordEncoder.encode("123456"))
                .email("sv001@student.edu.vn")
                .roles(Set.of(studentRole))
                .isActive(true)
                .build();
        userRepository.save(user1);

        Student s1 = Student.builder().studentCode("SV001").fullName("Nguyễn Hoàng Nam").gender("1")
                .user(user1)
                .academicYear(years.get(0)).department(depts.get(0)).major(majors.get(0))
                .trainingProgram(programs.get(0)).status("STUDYING").isActive(true).build();

        // SV2
        Users user2 = Users.builder()
                .username("sv002")
                .password(passwordEncoder.encode("123456"))
                .email("sv002@student.edu.vn")
                .roles(Set.of(studentRole))
                .isActive(true)
                .build();
        userRepository.save(user2);

        Student s2 = Student.builder().studentCode("SV002").fullName("Phạm Minh Hằng").gender("2").academicYear(years.get(0))
                .user(user2)
                .department(depts.get(0)).major(majors.get(0)).trainingProgram(programs.get(0))
                .status("STUDYING").isActive(true).build();

        return studentRepository.saveAll(List.of(s1, s2));
    }

    private List<CourseSection> seedCourseSections(List<Department> depts, List<AcademicYear> years, List<Major> majors) {
        if (courseSectionRepository.count() > 0) return courseSectionRepository.findAll();
        
        List<Course> courses = courseRepository.findAll();
        List<Semester> semesters = semesterRepository.findAll();

        CourseSection sec1 = CourseSection.builder()
                .classCode("L01_JAVA")
                .course(courses.get(0))
                .semester(semesters.get(0))
                .classType("Theory")
                .status("OPEN")
                .isActive(true)
                .build();

        CourseSection sec2 = CourseSection.builder()
                .classCode("L02_DB")
                .course(courses.get(1))
                .semester(semesters.get(0))
                .classType("Practice")
                .status("OPEN")
                .isActive(true)
                .build();

        return courseSectionRepository.saveAll(List.of(sec1, sec2));
    }

    private void seedSchedules(List<CourseSection> sections) {
        if (scheduleRepository.count() > 0) return;

        Building b1 = buildingRepository.save(Building.builder().buildingCode("A2").buildingName("Tòa nhà A2").build());
        Room r1 = roomRepository.save(Room.builder().roomCode("A2-502").roomName("Phòng 502").building(b1).build());

        Schedule sch1 = Schedule.builder()
                .courseSection(sections.get(0))
                .room(r1)
                .dayOfWeek(2) // Thứ 2
                .startPeriod(1)
                .endPeriod(4)
                .shift("MORNING")
                .build();

        Schedule sch2 = Schedule.builder()
                .courseSection(sections.get(1))
                .room(r1)
                .dayOfWeek(4) // Thứ 4
                .startPeriod(5)
                .endPeriod(8)
                .shift("AFTERNOON")
                .build();

        scheduleRepository.saveAll(List.of(sch1, sch2));
    }

    private void seedCourseRegistrations(List<Student> students, List<CourseSection> sections) {
        if (courseRegistrationRepository.count() > 0) return;

        CourseRegistration reg1 = CourseRegistration.builder()
                .student(students.get(0))
                .courseSection(sections.get(0))
                .status(1) // 1: Success
                .isPaid(true)
                .build();

        CourseRegistration reg2 = CourseRegistration.builder()
                .student(students.get(0))
                .courseSection(sections.get(1))
                .status(1)
                .isPaid(true)
                .build();

        courseRegistrationRepository.saveAll(List.of(reg1, reg2));
    }

    private void seedTuitionFees(List<TrainingProgram> programs) {
        if (tuitionFeeRepository.count() > 0)
            return;
        tuitionFeeRepository.saveAll(List.of(
                TuitionFee.builder().trainingProgram(programs.get(0)).courseYear("2023")
                        .pricePerCredit(BigDecimal.valueOf(450000)).baseTuition(BigDecimal.valueOf(15000000))
                        .isActive(true).build(),
                TuitionFee.builder().trainingProgram(programs.get(1)).courseYear("2023")
                        .pricePerCredit(BigDecimal.valueOf(600000)).baseTuition(BigDecimal.valueOf(25000000))
                        .isActive(true).build()));
    }
}
