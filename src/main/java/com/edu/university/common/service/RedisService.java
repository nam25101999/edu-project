package com.edu.university.common.service;

import com.edu.university.common.exception.BusinessException;
import com.edu.university.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // Key prefixes for OTP rate limiting
    private static final String KEY_COOLDOWN = "otp:cooldown:";
    private static final String KEY_LIMIT_USER = "otp:limit:user:";
    private static final String KEY_LIMIT_IP = "otp:limit:ip:";

    /**
     * Validate OTP request using multiple rate limiting layers.
     * @param email The target email
     * @param ip The client IP address
     * @throws BusinessException if any limit is exceeded
     */
    public void validateOtpRequest(String email, String ip) {
        checkCooldown(email);
        checkUserRateLimit(email);
        checkIpRateLimit(ip);
    }

    /**
     * Layer 1: Cooldown (1 request per 60 seconds)
     */
    private void checkCooldown(String email) {
        String key = KEY_COOLDOWN + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.info("OTP Cooldown triggered for email: {}", email);
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "Mỗi email chỉ được gửi OTP một lần mỗi phút.");
        }
        // Set cooldown key with 60s TTL
        redisTemplate.opsForValue().set(key, "1", 60, TimeUnit.SECONDS);
    }

    /**
     * Layer 2: User Rate Limit (Max 5 requests per 10 minutes)
     */
    private void checkUserRateLimit(String email) {
        String key = KEY_LIMIT_USER + email;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == null || count == 1) {
            redisTemplate.expire(key, 600, TimeUnit.SECONDS);
        }

        if (count != null && count > 5) {
            log.info("User OTP limit exceeded for email: {}", email);
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "Bạn đã vượt quá giới hạn gửi OTP (5 lần/10 phút).");
        }
    }

    /**
     * Layer 3: IP Rate Limit (Max 10 requests per 10 minutes)
     */
    private void checkIpRateLimit(String ip) {
        String key = KEY_LIMIT_IP + ip;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null || count == 1) {
            redisTemplate.expire(key, 600, TimeUnit.SECONDS);
        }

        if (count != null && count > 10) {
            log.warn("IP Spam detected from IP: {}. Requests in 10m: {}", ip, count);
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, "Hệ thống phát hiện dấu hiệu spam từ IP của bạn. Vui lòng thử lại sau.");
        }
    }

    /**
     * Optional: Clear rate limit after successful OTP verification (if needed)
     */
    public void clearRateLimit(String email) {
        redisTemplate.delete(KEY_COOLDOWN + email);
        redisTemplate.delete(KEY_LIMIT_USER + email);
    }
}
