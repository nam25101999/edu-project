package com.edu.university.modules.examination.repository;

import com.edu.university.modules.examination.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    Page<Exam> findBySemesterId(UUID semesterId, Pageable pageable);
    Page<Exam> findByCourseClassId(UUID courseClassId, Pageable pageable);
}
