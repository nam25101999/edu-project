package com.edu.university.modules.elearning.repository;

import com.edu.university.modules.elearning.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    Page<Submission> findByAssignmentId(UUID assignmentId, Pageable pageable);
    Optional<Submission> findByAssignmentIdAndStudentId(UUID assignmentId, UUID studentId);
}
