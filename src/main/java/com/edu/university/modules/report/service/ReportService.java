package com.edu.university.modules.report.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.enrollment.entity.Grade;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import com.edu.university.modules.enrollment.service.GradeService;
import com.edu.university.modules.course.repository.ClassSectionRepository;
import com.edu.university.modules.course.repository.CourseRepository;
import com.edu.university.modules.report.dto.ReportDtos.*;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service xử lý các báo cáo thống kê và dữ liệu Dashboard.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final ClassSectionRepository classSectionRepo;
    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;
    private final GradeService gradeService;

    public List<FacultyStat> getStudentsByFaculty() {
        List<Student> students = studentRepo.findAll();

        Map<String, Long> counts = students.stream()
                .filter(s -> s.getMajor() != null && s.getMajor().getFaculty() != null)
                .collect(Collectors.groupingBy(s -> s.getMajor().getFaculty().getName(), Collectors.counting()));

        return counts.entrySet().stream()
                .map(e -> new FacultyStat(e.getKey(), e.getValue()))
                .toList();
    }

    public PassFailStat getPassFailRatio() {
        List<Grade> gradedEnrollments = gradeRepo.findAll().stream()
                .filter(g -> g.getTotalScore() != null)
                .toList();

        if (gradedEnrollments.isEmpty()) {
            return new PassFailStat(0L, 0L, 0.0, 0.0);
        }

        long passCount = gradedEnrollments.stream()
                .filter(g -> g.getTotalScore() >= 4.0)
                .count();
        long failCount = gradedEnrollments.size() - passCount;

        double passRate = Math.round(((double) passCount / gradedEnrollments.size()) * 100.0 * 100.0) / 100.0;
        double failRate = Math.round(((double) failCount / gradedEnrollments.size()) * 100.0 * 100.0) / 100.0;

        return new PassFailStat(passCount, failCount, passRate, failRate);
    }

    public List<TopStudent> getTopStudents(int limit) {
        if (limit <= 0 || limit > 100) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "Giới hạn danh sách phải từ 1 đến 100");
        }

        return studentRepo.findAll().stream()
                .map(s -> new TopStudent(
                        s.getId(),
                        s.getStudentCode(),
                        s.getFullName(),
                        gradeService.calculateCumulativeGPA(s.getId())
                ))
                .sorted(Comparator.comparing(TopStudent::gpa).reversed())
                .limit(limit)
                .toList();
    }

    public DashboardOverview getDashboardOverview() {
        long totalStudents = studentRepo.count();
        long totalCourses = courseRepo.count();
        long totalClasses = classSectionRepo.count();
        long totalLecturers = userRepo.countByRole(Role.ROLE_LECTURER);

        return new DashboardOverview(totalStudents, totalLecturers, totalCourses, totalClasses);
    }
}