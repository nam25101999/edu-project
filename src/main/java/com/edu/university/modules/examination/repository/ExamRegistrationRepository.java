package com.edu.university.modules.examination.repository;

import com.edu.university.modules.examination.entity.ExamRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamRegistrationRepository extends JpaRepository<ExamRegistration, UUID> {
    Page<ExamRegistration> findByExamId(UUID examId, Pageable pageable);
    Page<ExamRegistration> findByStudentId(UUID studentId, Pageable pageable);
    Page<ExamRegistration> findByExamRoomId(UUID examRoomId, Pageable pageable);
}
