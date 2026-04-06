package com.edu.university.modules.elearning.repository;

import com.edu.university.modules.elearning.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findByAssignmentId(UUID assignmentId);
    Optional<Submission> findByAssignmentIdAndStudentId(UUID assignmentId, UUID studentId);
}
