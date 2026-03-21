package com.edu.university.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API Hệ thống Quản lý Sinh viên",
                version = "1.0",
                description = "Tài liệu API cho hệ thống tín chỉ đại học"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "Nhập JWT Token của bạn vào đây (không cần thêm chữ Bearer ở trước)"
)
public class OpenApiConfig {
    // File cấu hình này giúp Swagger UI hiển thị nút Authorize để nhập JWT Token
}