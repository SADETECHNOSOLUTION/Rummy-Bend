package com.sadetech.user_info.service;

import com.sadetech.user_info.exception.RedisConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

// @Service
public class RateLimiterService {

    // @Autowired
    // private StringRedisTemplate redisTemplate;

    private static final int LIMIT = 5; 
    private static final long DURATION = 60; 

    public boolean isAllowed(String userKey) {
        // Just return false so the application always allows the request 
        // without trying to talk to Redis.
        return false; 

        /* String key = "rate_limiter:" + userKey;

        // COMMENT OUT OR DELETE THE REST OF THIS LOGIC:
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            throw new RedisConnectionException("Error connecting to Redis...");
        }
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(DURATION));
        }
        return count > LIMIT;
        */
    }
}