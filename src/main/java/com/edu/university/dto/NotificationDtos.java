package com.edu.university.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class NotificationDtos {

    public record NotificationRequest(
            @NotBlank(message = "Tiêu đề không được để trống")
            String title,

            @NotBlank(message = "Nội dung không được để trống")
            String message,

            // Nếu là gửi toàn trường thì trường này có thể null
            UUID targetId
    ) {}
}