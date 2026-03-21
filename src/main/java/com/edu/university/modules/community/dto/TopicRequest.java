package com.edu.university.modules.community.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record TopicRequest(
        @NotBlank(message = "Tiêu đề không được để trống") String title,
        @NotBlank(message = "Nội dung bài viết không được để trống") String content,
        UUID courseId // Có thể null nếu đăng vào diễn đàn chung
) {}