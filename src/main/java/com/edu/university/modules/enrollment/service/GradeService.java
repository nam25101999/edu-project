package com.edu.university.modules.enrollment.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.enrollment.dto.GradeRequest;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.enrollment.entity.Enrollment;
import com.edu.university.modules.enrollment.entity.Grade;
import com.edu.university.modules.course.entity.Course;
import com.edu.university.modules.enrollment.repository.EnrollmentRepository;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service quản lý điểm số và tính toán GPA.
 */
@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepo;
    private final EnrollmentRepository enrollmentRepo;

    @LogAction(action = "ENTER_GRADE", entityName = "GRADE")
    @Transactional
    public Grade enterGrade(UUID enrollmentId, GradeRequest request) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // Lấy bản ghi điểm cũ hoặc tạo mới nếu chưa có
        Grade grade = gradeRepo.findByEnrollmentId(enrollmentId).orElse(new Grade());
        grade.setEnrollment(enrollment);
        grade.setAttendanceScore(request.attendance());
        grade.setMidtermScore(request.midterm());
        grade.setFinalScore(request.finalScore());

        // Tính điểm tổng (10% chuyên cần, 30% giữa kỳ, 60% cuối kỳ)
        double total = request.attendance() * 0.1 + request.midterm() * 0.3 + request.finalScore() * 0.6;
        grade.setTotalScore(Math.round(total * 10.0) / 10.0);

        // Quy đổi điểm GPA (hệ 4) và điểm chữ
        grade.setGpaScore(convertToGpa(total));
        grade.setLetterGrade(convertToLetter(total));

        return gradeRepo.save(grade);
    }

    public double calculateCumulativeGPA(UUID studentId) {
        List<Grade> grades = gradeRepo.findByEnrollmentStudentId(studentId);

        // Map để lưu điểm cao nhất của từng môn học (tránh tính trùng môn học lại)
        Map<UUID, Double> maxGpaPerCourse = new HashMap<>();
        Map<UUID, Integer> creditsPerCourse = new HashMap<>();

        for (Grade g : grades) {
            if (g.getGpaScore() != null) {
                Course course = g.getEnrollment().getClassSection().getCourse();
                UUID courseId = course.getId();
                double currentGpa = g.getGpaScore();

                if (!maxGpaPerCourse.containsKey(courseId) || currentGpa > maxGpaPerCourse.get(courseId)) {
                    maxGpaPerCourse.put(courseId, currentGpa);
                    creditsPerCourse.put(courseId, course.getCredits());
                }
            }
        }

        double totalPoints = 0;
        int totalCredits = 0;

        for (UUID courseId : maxGpaPerCourse.keySet()) {
            totalPoints += (maxGpaPerCourse.get(courseId) * creditsPerCourse.get(courseId));
            totalCredits += creditsPerCourse.get(courseId);
        }

        return totalCredits == 0 ? 0.0 : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    private double convertToGpa(double total) {
        if (total >= 8.5) return 4.0;
        if (total >= 7.0) return 3.0;
        if (total >= 5.5) return 2.0;
        if (total >= 4.0) return 1.0;
        return 0.0;
    }

    private String convertToLetter(double total) {
        if (total >= 8.5) return "A";
        if (total >= 7.0) return "B";
        if (total >= 5.5) return "C";
        if (total >= 4.0) return "D";
        return "F";
    }
}