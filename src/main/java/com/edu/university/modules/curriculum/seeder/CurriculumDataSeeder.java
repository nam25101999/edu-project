package com.edu.university.modules.curriculum.seeder;

import com.edu.university.common.seeder.ModuleSeeder;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.entity.Major;
import com.edu.university.modules.curriculum.entity.TrainingProgram;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.curriculum.repository.TrainingProgramRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.entity.Faculty;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.hr.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurriculumDataSeeder implements ModuleSeeder {

    private final MajorRepository majorRepository;
    private final CourseRepository courseRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public void seed() {
        log.info("Seeding Curriculum data...");
        if (majorRepository.count() > 0) return;

        List<Faculty> faculties = facultyRepository.findAll();
        List<Department> depts = departmentRepository.findAll();
        
        if (faculties.isEmpty() || depts.isEmpty()) {
            log.warn("Cannot seed majors: No faculties or departments found.");
            return;
        }

        List<Major> majors = seedMajors(faculties, depts);
        seedCourses(depts);
        seedTrainingPrograms(majors);
    }

    @Override
    public int getOrder() {
        return 40;
    }

    private List<Major> seedMajors(List<Faculty> faculties, List<Department> depts) {
        List<Major> majors = new ArrayList<>();
        String[][] majorData = {
                {"CNTT", "Khoa học máy tính", "Kỹ thuật phần mềm"},
                {"KTE", "Kinh tế đầu tư", "Kinh tế quốc tế"},
                {"NNGU", "Ngôn ngữ Anh", "Ngôn ngữ Nhật"},
                {"CKDT", "Cơ điện tử", "Kỹ thuật máy móc"},
                {"KHCB", "Toán ứng dụng", "Vật lý lý thuyết"},
                {"LUAT", "Luật kinh tế", "Luật dân sự"},
                {"DUOC", "Dược lâm sàng", "Dược liệu học"},
                {"QUAN", "Quản trị nhân lực", "Marketing"},
                {"MTHT", "Kỹ thuật môi trường", "Quản lý tài nguyên"},
                {"XAYD", "Xây dựng dân dụng", "Kiến trúc"}
        };

        for (int i = 0; i < faculties.size(); i++) {
            Faculty f = faculties.get(i);
            Department d = depts.get(i % depts.size());
            String[] mNames = majorData[i % majorData.length];
            
            for (int j = 1; j <= 2; j++) {
                String mName = mNames[j % mNames.length];
                String mCode = f.getCode() + "_M" + j;
                majors.add(Major.builder()
                        .majorCode(mCode)
                        .name(mName)
                        .faculty(f)
                        .department(d)
                        .effectiveDate("2023-09-01")
                        .isActive(true)
                        .build());
            }
        }
        return majorRepository.saveAll(majors);
    }

    private void seedCourses(List<Department> depts) {
        if (courseRepository.count() > 0) return;

        List<Course> courses = List.of(
                Course.builder().courseCode("JAVA01").name("Lập trình Java Cơ bản").credits(BigDecimal.valueOf(3)).department(depts.get(0)).isActive(true).build(),
                Course.builder().courseCode("DB01").name("Cơ sở dữ liệu").credits(BigDecimal.valueOf(3)).department(depts.get(0)).isActive(true).build(),
                Course.builder().courseCode("DSA01").name("Cấu trúc dữ liệu và Giải thuật").credits(BigDecimal.valueOf(3)).department(depts.get(0)).isActive(true).build(),
                Course.builder().courseCode("WEB01").name("Lập trình Web Frontend").credits(BigDecimal.valueOf(3)).department(depts.get(0)).isActive(true).build(),
                Course.builder().courseCode("MKT01").name("Marketing căn bản").credits(BigDecimal.valueOf(2)).department(depts.get(1)).isActive(true).build(),
                Course.builder().courseCode("ECO01").name("Kinh tế vĩ mô").credits(BigDecimal.valueOf(3)).department(depts.get(1)).isActive(true).build(),
                Course.builder().courseCode("ENG01").name("Tiếng Anh giao tiếp 1").credits(BigDecimal.valueOf(3)).department(depts.get(2)).isActive(true).build(),
                Course.builder().courseCode("ENG02").name("Tiếng Anh giao tiếp 2").credits(BigDecimal.valueOf(3)).department(depts.get(2)).isActive(true).build(),
                Course.builder().courseCode("MATH01").name("Toán cao cấp A1").credits(BigDecimal.valueOf(3)).department(depts.get(4)).isActive(true).build(),
                Course.builder().courseCode("MATH02").name("Toán cao cấp A2").credits(BigDecimal.valueOf(3)).department(depts.get(4)).isActive(true).build(),
                Course.builder().courseCode("PHY01").name("Vật lý đại cương 1").credits(BigDecimal.valueOf(3)).department(depts.get(4)).isActive(true).build(),
                Course.builder().courseCode("NET01").name("Mạng máy tính").credits(BigDecimal.valueOf(3)).department(depts.get(0)).isActive(true).build(),
                Course.builder().courseCode("OS01").name("Hệ điều hành").credits(BigDecimal.valueOf(3)).department(depts.get(0)).isActive(true).build(),
                Course.builder().courseCode("AI01").name("Trí tuệ nhân tạo").credits(BigDecimal.valueOf(3)).department(depts.get(0)).isActive(true).build(),
                Course.builder().courseCode("CIR01").name("Mạch điện tử").credits(BigDecimal.valueOf(3)).department(depts.get(3)).isActive(true).build(),
                Course.builder().courseCode("ENG_TOEIC").name("Luyện thi TOEIC-550").credits(BigDecimal.valueOf(4)).department(depts.get(2)).isActive(true).build());
        courseRepository.saveAll(courses);
    }

    private void seedTrainingPrograms(List<Major> majors) {
        if (trainingProgramRepository.count() > 0) return;
        List<TrainingProgram> programs = new ArrayList<>();
        for (Major major : majors) {
            programs.add(TrainingProgram.builder()
                    .programCode("CT-" + major.getMajorCode())
                    .programName("Chương trình chuẩn " + major.getName())
                    .major(major).build());
        }
        trainingProgramRepository.saveAll(programs);
    }
}
