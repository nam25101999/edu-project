package com.edu.university.modules.community.entity;

import com.edu.university.modules.auth.entity.User;
import com.edu.university.modules.enrollment.repository.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "forum_topics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ForumTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Nếu course = null thì đây là bài đăng ở Diễn đàn chung
    // Nếu có course thì đây là bài Hỏi đáp của riêng Môn học
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}