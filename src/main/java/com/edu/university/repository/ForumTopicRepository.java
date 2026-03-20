package com.edu.university.repository;

import com.edu.university.entity.ForumTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ForumTopicRepository extends JpaRepository<ForumTopic, UUID> {

    // Lấy danh sách Topic của Diễn đàn chung (Không thuộc môn nào)
    List<ForumTopic> findByCourseIsNullOrderByCreatedAtDesc();

    // Lấy danh sách Topic Hỏi đáp theo Môn học
    List<ForumTopic> findByCourseIdOrderByCreatedAtDesc(UUID courseId);
}