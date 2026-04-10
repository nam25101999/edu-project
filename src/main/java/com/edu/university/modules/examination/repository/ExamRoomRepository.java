package com.edu.university.modules.examination.repository;

import com.edu.university.modules.examination.entity.ExamRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExamRoomRepository extends JpaRepository<ExamRoom, UUID> {
    Page<ExamRoom> findByExamId(UUID examId, Pageable pageable);
    Page<ExamRoom> findByRoomId(UUID roomId, Pageable pageable);
}
