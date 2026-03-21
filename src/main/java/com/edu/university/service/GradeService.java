package com.edu.university.service;

import com.edu.university.annotation.LogAction;
import com.edu.university.dto.PayloadDtos.GradeRequest;
import com.edu.university.entity.Enrollment;
import com.edu.university.entity.Grade;
import com.edu.university.repository.EnrollmentRepository;
import com.edu.university.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepo;
    private final EnrollmentRepository enrollmentRepo;

    @LogAction(action = "ENTER_GRADE", entityName = "GRADE")
    @Transactional
    public Grade enterGrade(UUID enrollmentId, GradeRequest request) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký lớp học phần"));

        Grade grade = gradeRepo.findByEnrollmentId(enrollmentId).orElse(new Grade());
        grade.setEnrollment(enrollment);
        grade.setAttendanceScore(request.attendance());
        grade.setMidtermScore(request.midterm());
        grade.setFinalScore(request.finalScore());

        // Tính điểm tổng (10% chuyên cần, 30% giữa kỳ, 60% cuối kỳ)
        double total = request.attendance() * 0.1 + request.midterm() * 0.3 + request.finalScore() * 0.6;
        grade.setTotalScore(Math.round(total * 10.0) / 10.0);

        // Quy đổi điểm GPA (hệ 4) và điểm chữ (A, B, C, D, F)
        grade.setGpaScore(convertToGpa(total));
        grade.setLetterGrade(convertToLetter(total));

        return gradeRepo.save(grade);
    }

    public double calculateCumulativeGPA(UUID studentId) {
        List<Grade> grades = gradeRepo.findByEnrollmentStudentId(studentId);

        // --- LOGIC: TÍNH GPA TỰ ĐỘNG LẤY ĐIỂM CAO NHẤT ---
        Map<UUID, Double> maxGpaPerCourse = new HashMap<>();
        Map<UUID, Integer> creditsPerCourse = new HashMap<>();

        for (Grade g : grades) {
            if (g.getGpaScore() != null) {
                com.edu.university.entity.Course course = g.getEnrollment().getClassSection().getCourse();
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