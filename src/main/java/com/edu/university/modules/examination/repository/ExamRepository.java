package com.edu.university.modules.examination.repository;

import com.edu.university.modules.examination.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findBySemesterId(UUID semesterId);
    List<Exam> findByCourseClassId(UUID courseClassId);
}
