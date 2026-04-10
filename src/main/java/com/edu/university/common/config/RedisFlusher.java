package com.edu.university.common.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisFlusher {

    @Bean
    public CommandLineRunner flushRedis(StringRedisTemplate template) {
        return args -> {
            try {
                template.getConnectionFactory().getConnection().serverCommands().flushAll();
                System.out.println("====== REDIS CACHE FLUSHED SUCCESSFULLY ======");
            } catch (Exception e) {
                System.err.println("Failed to flush redis caching: " + e.getMessage());
            }
        };
    }
}
