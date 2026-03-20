package com.edu.university.repository;

import com.edu.university.entity.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ForumCommentRepository extends JpaRepository<ForumComment, UUID> {
    List<ForumComment> findByTopicIdOrderByCreatedAtAsc(UUID topicId);
}