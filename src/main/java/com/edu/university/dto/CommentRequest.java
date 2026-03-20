package com.edu.university.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank(message = "Nội dung bình luận không được để trống") String content
) {}