package com.edu.university.modules.finance.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.finance.entity.Scholarship;
import com.edu.university.modules.finance.repository.ScholarshipRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScholarshipService {

    private final ScholarshipRepository scholarshipRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;

    @Transactional
    public Scholarship grantScholarship(UUID studentId, UUID semesterId, String name, BigDecimal amount) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy học kỳ"));

        Scholarship scholarship = Scholarship.builder()
                .student(student)
                .semester(semester)
                .name(name)
                .amount(amount)
                .status("GRANTED")
                .build();

        return scholarshipRepository.save(scholarship);
    }

    public List<Scholarship> getStudentScholarships(UUID studentId) {
        return scholarshipRepository.findByStudentId(studentId);
    }
}
