package com.edu.university.modules.registration.seeder;

import com.edu.university.common.seeder.ModuleSeeder;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.registration.entity.CourseRegistration;
import com.edu.university.modules.registration.repository.CourseRegistrationRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationDataSeeder implements ModuleSeeder {

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final StudentRepository studentRepository;
    private final CourseSectionRepository courseSectionRepository;

    @Override
    public void seed() {
        log.info("Seeding Registration data...");
        if (courseRegistrationRepository.count() > 0) return;

        List<Student> students = studentRepository.findAll();
        List<CourseSection> sections = courseSectionRepository.findAll();

        if (students.isEmpty() || sections.isEmpty()) {
            log.warn("Cannot seed registrations: No students or course sections found.");
            return;
        }

        List<CourseRegistration> registrations = new ArrayList<>();
        // Each student automatically registers for 4-6 courses
        for (int i = 0; i < students.size(); i++) {
            int courseCount = 4 + (i % 3);
            for (int j = 0; j < courseCount; j++) {
                registrations.add(CourseRegistration.builder()
                        .student(students.get(i))
                        .courseSection(sections.get((i + j) % sections.size()))
                        .status(1) // 1: Success
                        .isPaid(i % 5 != 0) // Some unpaid for testing
                        .build());
            }
        }
        courseRegistrationRepository.saveAll(registrations);
    }

    @Override
    public int getOrder() {
        return 60;
    }
}
