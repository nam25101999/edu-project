package com.edu.university.modules.report.service;

import com.edu.university.modules.auth.repository.UserRepository;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import com.edu.university.modules.course.repository.ClassSectionRepository;
import com.edu.university.modules.course.repository.CourseRepository;
import com.edu.university.modules.enrollment.service.GradeService;
import com.edu.university.modules.report.dto.ReportDtos.*;
import com.edu.university.modules.enrollment.entity.Grade;
import com.edu.university.modules.auth.entity.Role;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final ClassSectionRepository classSectionRepo;
    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;
    private final GradeService gradeService;

    // ======================================
    // 1. SỐ LƯỢNG SINH VIÊN THEO KHOA
    // ======================================
    public List<FacultyStat> getStudentsByFaculty() {
        List<Student> students = studentRepo.findAll();

        // Nhóm sinh viên theo khoa thông qua liên kết Student -> Major -> Faculty
        Map<String, Long> counts = students.stream()
                .filter(s -> s.getMajor() != null && s.getMajor().getFaculty() != null)
                .collect(Collectors.groupingBy(s -> s.getMajor().getFaculty().getName(), Collectors.counting()));

        return counts.entrySet().stream()
                .map(e -> new FacultyStat(e.getKey(), e.getValue()))
                .toList();
    }

    // ======================================
    // 2. TỶ LỆ ĐẬU / RỚT
    // ======================================
    public PassFailStat getPassFailRatio() {
        List<Grade> gradedEnrollments = gradeRepo.findAll().stream()
                .filter(g -> g.getTotalScore() != null)
                .toList();

        long passCount = gradedEnrollments.stream()
                .filter(g -> g.getTotalScore() >= 4.0)
                .count();
        long failCount = gradedEnrollments.size() - passCount;

        // Tính phần trăm (%)
        double passRate = gradedEnrollments.isEmpty() ? 0.0 :
                Math.round(((double) passCount / gradedEnrollments.size()) * 100.0 * 100.0) / 100.0;
        double failRate = gradedEnrollments.isEmpty() ? 0.0 :
                Math.round(((double) failCount / gradedEnrollments.size()) * 100.0 * 100.0) / 100.0;

        return new PassFailStat(passCount, failCount, passRate, failRate);
    }

    // ======================================
    // 3. TOP SINH VIÊN CÓ GPA CAO NHẤT
    // ======================================
    public List<TopStudent> getTopStudents(int limit) {
        return studentRepo.findAll().stream()
                .map(s -> new TopStudent(
                        s.getId(),
                        s.getStudentCode(),
                        s.getFullName(),
                        gradeService.calculateCumulativeGPA(s.getId()) // Tái sử dụng logic tính GPA
                ))
                .sorted(Comparator.comparing(TopStudent::gpa).reversed()) // Giảm dần theo GPA
                .limit(limit)
                .toList();
    }

    // ======================================
    // 4. DASHBOARD TỔNG QUAN HỆ THỐNG
    // ======================================
    public DashboardOverview getDashboardOverview() {
        long totalStudents = studentRepo.count();
        long totalCourses = courseRepo.count();
        long totalClasses = classSectionRepo.count();

        // Đếm số lượng User có Role là GIẢNG VIÊN
        long totalLecturers = userRepo.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_LECTURER)
                .count();

        return new DashboardOverview(totalStudents, totalLecturers, totalCourses, totalClasses);
    }
}