package com.edu.university.modules.finance.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.academic.entity.Semester;
import com.edu.university.modules.academic.repository.SemesterRepository;
import com.edu.university.modules.finance.dto.response.ScholarshipResponseDTO;
import com.edu.university.modules.finance.entity.Scholarship;
import com.edu.university.modules.finance.repository.ScholarshipRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScholarshipService {

    private final ScholarshipRepository scholarshipRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;

    @Transactional
    public ScholarshipResponseDTO grantScholarship(UUID studentId, UUID semesterId, String name, BigDecimal amount) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEMESTER_NOT_FOUND));

        Scholarship scholarship = Scholarship.builder()
                .student(student)
                .semester(semester)
                .name(name)
                .amount(amount)
                .status("GRANTED")
                .build();

        Scholarship saved = scholarshipRepository.save(scholarship);
        return mapToResponseDTO(saved);
    }

    public Page<ScholarshipResponseDTO> getStudentScholarships(UUID studentId, Pageable pageable) {
        return scholarshipRepository.findByStudentId(studentId, pageable).map(this::mapToResponseDTO);
    }

    private ScholarshipResponseDTO mapToResponseDTO(Scholarship scholarship) {
        return ScholarshipResponseDTO.builder()
                .id(scholarship.getId())
                .studentId(scholarship.getStudent().getId())
                .studentName(scholarship.getStudent().getFullName())
                .semesterId(scholarship.getSemester().getId())
                .semesterName(scholarship.getSemester().getSemesterName())
                .name(scholarship.getName())
                .amount(scholarship.getAmount())
                .status(scholarship.getStatus())
                .build();
    }
}
