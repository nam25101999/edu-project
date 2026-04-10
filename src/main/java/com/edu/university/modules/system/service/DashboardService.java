package com.edu.university.modules.system.service;

import com.edu.university.modules.auth.repository.RoleRepository;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.curriculum.repository.CourseRepository;
import com.edu.university.modules.curriculum.repository.MajorRepository;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.system.dto.response.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final MajorRepository majorRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getStats() {
        long studentCount = studentRepository.count();
        long courseCount = courseRepository.count();
        long majorCount = majorRepository.count();
        
        // Count Lecturers specifically if the role exists (e.g., ROLE_LECTURER or LECTURER)
        // Check for LECTURER first, fallback to 0 if not found
        long lecturerCount = roleRepository.findByName("LECTURER")
                .map(userRepository::countByRolesContaining)
                .orElse(0L);

        return DashboardStatsResponse.builder()
                .totalStudents(studentCount)
                .totalLecturers(lecturerCount)
                .totalCourses(courseCount)
                .totalMajors(majorCount)
                .build();
    }
}
