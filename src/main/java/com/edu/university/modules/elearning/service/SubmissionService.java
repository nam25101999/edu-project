package com.edu.university.modules.elearning.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import com.edu.university.modules.elearning.entity.Assignment;
import com.edu.university.modules.elearning.entity.Submission;
import com.edu.university.modules.elearning.repository.AssignmentRepository;
import com.edu.university.modules.elearning.repository.SubmissionRepository;
import com.edu.university.modules.student.entity.Student;
import com.edu.university.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Submission createSubmission(UUID assignmentId, UUID studentId, String content, String fileUrl) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));

        Submission submission = submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .orElse(Submission.builder()
                        .assignment(assignment)
                        .student(student)
                        .build());

        submission.setContent(content);
        submission.setFileUrl(fileUrl);
        submission.setSubmittedAt(LocalDateTime.now());
        
        return submissionRepository.save(submission);
    }

    @Transactional
    public Submission gradeSubmission(UUID submissionId, Double score, String feedback) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND));
        
        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setGraded(true);
        
        return submissionRepository.save(submission);
    }
}
