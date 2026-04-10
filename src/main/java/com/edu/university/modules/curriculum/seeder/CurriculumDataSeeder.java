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
        if (courseRepository.count() >= 50) return;

        List<Course> courses = new ArrayList<>();
        String[][] commonCourses = {
            {"MATH", "Toán cao cấp", "3"}, {"PHY", "Vật lý đại cương", "3"}, 
            {"ENG", "Tiếng Anh chuyên ngành", "3"}, {"POL", "Triết học Mác-Lênin", "2"},
            {"SOFT", "Kỹ năng mềm", "2"}, {"LAW", "Pháp luật đại cương", "2"}
        };
        
        // Add general courses for most departments
        for (int i = 0; i < 5; i++) {
            for (String[] data : commonCourses) {
                courses.add(Course.builder()
                        .courseCode(data[0] + (i + 1))
                        .name(data[1] + " " + (i + 1))
                        .credits(new BigDecimal(data[2]))
                        .department(depts.get(i % depts.size()))
                        .isActive(true).build());
            }
        }

        // Specific Computing courses
        String[] computingCourses = {"Java Programming", "Database Systems", "Web Development", "Data Structures", "Algorithms", "Networking", "Operating Systems", "Cloud Computing", "AI", "Cybersecurity"};
        for (int i = 0; i < computingCourses.length; i++) {
            courses.add(Course.builder()
                    .courseCode("IT_" + (i + 1))
                    .name(computingCourses[i])
                    .credits(BigDecimal.valueOf(3))
                    .department(depts.get(0))
                    .isActive(true).build());
        }

        // Specific Economy courses
        String[] economyCourses = {"Microeconomics", "Macroeconomics", "Accounting", "Marketing", "Finance", "Investment", "Audit", "Logistics"};
        for (int i = 0; i < economyCourses.length; i++) {
            courses.add(Course.builder()
                    .courseCode("EC_" + (i + 1))
                    .name(economyCourses[i])
                    .credits(BigDecimal.valueOf(3))
                    .department(depts.get(1))
                    .isActive(true).build());
        }

        // Add more until 60
        for (int i = 0; i < 15; i++) {
             courses.add(Course.builder()
                    .courseCode("SPEC_" + (i + 1))
                    .name("Học phần chuyên sâu " + (i + 1))
                    .credits(BigDecimal.valueOf(4))
                    .department(depts.get(i % depts.size()))
                    .isActive(true).build());
        }

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
