package com.edu.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageRequest(
        @NotNull(message = "ID người nhận không được để trống") UUID receiverId,
        @NotBlank(message = "Nội dung tin nhắn không được để trống") String content
) {}