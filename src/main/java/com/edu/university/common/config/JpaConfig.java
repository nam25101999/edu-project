package com.edu.university.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Kích hoạt Auditing
public class JpaConfig {

    /**
     * Bean này giúp Spring biết được "ai" đang thực hiện hành động
     * để tự động điền vào các trường @CreatedBy và @LastModifiedBy
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.of("SYSTEM"); // Hoặc trả về Optional.empty() tùy logic của bạn
            }

            // Trả về username của người đang đăng nhập
            return Optional.of(authentication.getName());
        };
    }
}