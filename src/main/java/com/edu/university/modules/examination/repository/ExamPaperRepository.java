package com.edu.university.modules.examination.repository;

import com.edu.university.modules.examination.entity.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, UUID> {
    List<ExamPaper> findByExamId(UUID examId);
}
