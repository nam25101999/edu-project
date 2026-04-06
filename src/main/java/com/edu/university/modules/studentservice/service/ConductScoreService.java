package com.edu.university.modules.studentservice.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import com.edu.university.modules.studentservice.entity.ConductScore;
import com.edu.university.modules.studentservice.repository.ConductScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConductScoreService {

    private final ConductScoreRepository conductScoreRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;

    @Transactional
    public ConductScore updateConductScore(UUID studentId, UUID semesterId, Integer score) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));

        ConductScore conductScore = conductScoreRepository.findByStudentIdAndSemesterId(studentId, semesterId)
                .orElse(ConductScore.builder()
                        .student(student)
                        .semester(semester)
                        .build());
        
        conductScore.setScore(score);
        conductScore.setGrade(calculateGrade(score));
        
        return conductScoreRepository.save(conductScore);
    }

    private String calculateGrade(Integer score) {
        if (score >= 90) return "XUẤT SẮC";
        if (score >= 80) return "TỐT";
        if (score >= 65) return "KHÁ";
        if (score >= 50) return "TRUNG BÌNH";
        if (score >= 35) return "YẾU";
        return "KÉM";
    }
}
