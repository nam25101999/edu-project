package com.edu.university.common.config;

import com.edu.university.common.security.AuditorAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Kích hoạt Auditing
public class JpaConfig {

    /**
     * Bean này giúp Spring biết được "ai" đang thực hiện hành động
     * để tự động điền vào các trường @CreatedBy và @LastModifiedBy
     */
    @Bean
    public AuditorAware<String> auditorProvider(AuditorAwareImpl auditorAwareImpl) {
        return auditorAwareImpl;
    }
}