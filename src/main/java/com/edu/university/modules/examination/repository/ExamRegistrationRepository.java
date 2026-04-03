package com.edu.university.modules.examination.repository;

import com.edu.university.modules.examination.entity.ExamRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamRegistrationRepository extends JpaRepository<ExamRegistration, UUID> {
    List<ExamRegistration> findByExamId(UUID examId);
    List<ExamRegistration> findByStudentId(UUID studentId);
    List<ExamRegistration> findByExamRoomId(UUID examRoomId);
}
