package com.edu.university.modules.enrollment.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.enrollment.dto.GradeGpaDto;
import com.edu.university.modules.enrollment.dto.GradeRequest;
import com.edu.university.modules.enrollment.dto.GradeResponse;
import com.edu.university.modules.report.annotation.LogAction;
import com.edu.university.modules.enrollment.entity.Enrollment;
import com.edu.university.modules.enrollment.entity.Grade;
import com.edu.university.modules.enrollment.repository.EnrollmentRepository;
import com.edu.university.modules.enrollment.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepo;
    private final EnrollmentRepository enrollmentRepo;

    @LogAction(action = "ENTER_GRADE", entityName = "GRADE")
    @Transactional
    public GradeResponse enterGrade(UUID enrollmentId, GradeRequest request) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        Grade grade = gradeRepo.findByEnrollmentId(enrollmentId)
                .orElse(new Grade());

        grade.setEnrollment(enrollment);
        grade.setAttendanceScore(request.attendance());
        grade.setMidtermScore(request.midterm());
        grade.setFinalScore(request.finalScore());

        double total = request.attendance() * 0.1
                + request.midterm() * 0.3
                + request.finalScore() * 0.6;

        total = Math.round(total * 10.0) / 10.0;
        grade.setTotalScore(total);

        grade.setGpaScore(convertToGpa(total));
        grade.setLetterGrade(convertToLetter(total));

        Grade savedGrade = gradeRepo.save(grade);

        return mapToResponse(savedGrade);
    }

    /**
     * Lấy danh sách điểm có phân trang và chuyển đổi sang DTO.
     * Đây là cách để fix lỗi LazyInitializationException trong PageImpl.
     */
    @Transactional(readOnly = true)
    public Page<GradeResponse> getAllGrades(Pageable pageable) {
        Page<Grade> gradePage = gradeRepo.findAll(pageable);

        // Sử dụng .map() của Page để chuyển đổi từng Entity sang DTO bên trong Transaction
        return gradePage.map(this::mapToResponse);
    }

    public double calculateCumulativeGPA(UUID studentId) {
        List<GradeGpaDto> grades = gradeRepo.findGpaData(studentId);

        Map<UUID, Double> maxGpaPerCourse = new HashMap<>();
        Map<UUID, Integer> creditsPerCourse = new HashMap<>();

        for (GradeGpaDto g : grades) {
            if (g.gpaScore() != null) {
                UUID courseId = g.courseId();
                if (!maxGpaPerCourse.containsKey(courseId)
                        || g.gpaScore() > maxGpaPerCourse.get(courseId)) {

                    maxGpaPerCourse.put(courseId, g.gpaScore());
                    creditsPerCourse.put(courseId, g.credits());
                }
            }
        }

        double totalPoints = 0;
        int totalCredits = 0;

        for (UUID courseId : maxGpaPerCourse.keySet()) {
            totalPoints += maxGpaPerCourse.get(courseId) * creditsPerCourse.get(courseId);
            totalCredits += creditsPerCourse.get(courseId);
        }

        return totalCredits == 0
                ? 0.0
                : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    /**
     * Helper method để map Grade Entity sang GradeResponse DTO.
     * Phải được gọi khi Hibernate Session còn mở.
     */
    private GradeResponse mapToResponse(Grade grade) {
        Enrollment enrollment = grade.getEnrollment();
        return new GradeResponse(
                grade.getId(),
                enrollment.getId(),
                enrollment.getStudent().getStudentCode(),
                enrollment.getStudent().getFullName(),
                enrollment.getStudent().getMajor() != null ? enrollment.getStudent().getMajor().getName() : "N/A",
                grade.getAttendanceScore(),
                grade.getMidtermScore(),
                grade.getFinalScore(),
                grade.getTotalScore(),
                grade.getGpaScore(),
                grade.getLetterGrade()
        );
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