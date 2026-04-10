package com.edu.university.modules.examination.repository;

import com.edu.university.modules.examination.entity.ExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, UUID> {
    Page<ExamPaper> findByExamId(UUID examId, Pageable pageable);
}
