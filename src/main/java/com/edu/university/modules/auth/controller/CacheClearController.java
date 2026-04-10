package com.edu.university.modules.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheClearController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/api/clear-redis")
    public String clear() {
        stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        return "Flushed all redis databases.";
    }
}
