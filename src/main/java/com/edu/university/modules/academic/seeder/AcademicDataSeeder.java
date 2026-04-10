package com.edu.university.modules.academic.seeder;

import com.edu.university.common.seeder.ModuleSeeder;
import com.edu.university.modules.academic.entity.AcademicYear;
import com.edu.university.modules.academic.entity.CourseSection;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.AcademicYearRepository;
import com.edu.university.modules.academic.repository.CourseSectionRepository;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.academic.service.AcademicYearService;
import com.edu.university.modules.schedule.entity.Schedule;
import com.edu.university.modules.schedule.repository.ScheduleRepository;
import com.edu.university.modules.curriculum.entity.Course;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.hr.entity.Department;
import com.edu.university.modules.hr.repository.DepartmentRepository;
import com.edu.university.modules.schedule.entity.Building;
import com.edu.university.modules.schedule.entity.Room;
import com.edu.university.modules.schedule.repository.BuildingRepository;
import com.edu.university.modules.schedule.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcademicDataSeeder implements ModuleSeeder {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicYearService academicYearService;
    private final SemesterRepository semesterRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;

    @Override
    public void seed() {
        log.info("Seeding Academic data...");
        if (courseSectionRepository.count() > 0) return;

        // Auto-generate years and semesters if empty
        academicYearService.getAll(org.springframework.data.domain.Pageable.unpaged());
        
        List<AcademicYear> years = academicYearRepository.findAll();
        List<Department> depts = departmentRepository.findAll();
        
        if (depts.isEmpty()) {
            log.warn("Cannot seed sections: No departments found.");
            return;
        }

        List<CourseSection> sections = seedCourseSections(depts, years);
        seedSchedules(sections);
    }

    @Override
    public int getOrder() {
        return 30;
    }

    private List<CourseSection> seedCourseSections(List<Department> depts, List<AcademicYear> years) {
        List<Course> courses = courseRepository.findAll();
        List<Semester> semesters = semesterRepository.findAll();
        if (courses.isEmpty() || semesters.isEmpty()) return new ArrayList<>();

        List<CourseSection> sections = new ArrayList<>();
        // Generate 50 classes from the available courses
        for (int i = 0; i < 50 && i < courses.size() * 2; i++) {
            Course course = courses.get(i % courses.size());
            String sectionCode = "SEC_" + String.format("%03d", i + 1) + "_" + course.getCourseCode();
            sections.add(CourseSection.builder()
                    .sectionCode(sectionCode)
                    .classCode(sectionCode)
                    .course(course)
                    .semester(semesters.get(i % Math.min(6, semesters.size()))) // Spread across semesters
                    .classType(i % 3 == 0 ? "Practice" : "Theory")
                    .status("OPEN")
                    .capacity(40)
                    .maxStudents(50)
                    .minStudents(10)
                    .isActive(true)
                    .build());
        }

        // Seed the protected TOEIC class
        courses.stream()
                .filter(c -> "ENG_TOEIC".equals(c.getCourseCode()))
                .findFirst()
                .ifPresent(toeicCourse -> {
                    sections.add(CourseSection.builder()
                            .sectionCode("TOEIC-550-SYS")
                            .classCode("TOEIC-550-SYS")
                            .course(toeicCourse)
                            .semester(semesters.get(0))
                            .classType("4 Kỹ năng")
                            .status("OPEN")
                            .isSystem(true)
                            .isActive(true)
                            .note("Lớp học hệ thống - Không thể xóa")
                            .build());
                });

        return courseSectionRepository.saveAll(sections);
    }

    private void seedSchedules(List<CourseSection> sections) {
        if (sections.isEmpty() || buildingRepository.count() >= 3) return;

        String[] buildings = {"A1", "A2", "B1", "C1"};
        List<Room> allRooms = new ArrayList<>();
        
        for (String bCode : buildings) {
            Building b = buildingRepository.save(Building.builder()
                    .buildingCode(bCode)
                    .buildingName("Tòa nhà " + bCode)
                    .build());
            
            for (int i = 1; i <= 10; i++) {
                allRooms.add(Room.builder()
                        .roomCode(bCode + "-" + (100 * (i % 5 + 1) + i))
                        .roomName("Phòng " + (100 * (i % 5 + 1) + i))
                        .building(b)
                        .capacity(i % 2 == 0 ? 40 : 80)
                        .isActive(true)
                        .build());
            }
        }
        roomRepository.saveAll(allRooms);

        List<Schedule> schedules = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            schedules.add(Schedule.builder()
                    .courseSection(sections.get(i))
                    .room(allRooms.get(i % allRooms.size()))
                    .dayOfWeek((i % 6) + 2) // Từ thứ 2 đến thứ 7
                    .startPeriod(i % 2 == 0 ? 1 : 7)
                    .endPeriod(i % 2 == 0 ? 4 : 10)
                    .shift(i % 2 == 0 ? "MORNING" : "AFTERNOON")
                    .build());
        }
        scheduleRepository.saveAll(schedules);
    }
}
