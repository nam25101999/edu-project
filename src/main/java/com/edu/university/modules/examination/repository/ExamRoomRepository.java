package com.edu.university.modules.examination.repository;

import com.edu.university.modules.examination.entity.ExamRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamRoomRepository extends JpaRepository<ExamRoom, UUID> {
    List<ExamRoom> findByExamId(UUID examId);
    List<ExamRoom> findByRoomId(UUID roomId);
}
