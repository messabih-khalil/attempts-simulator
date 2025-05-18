package com.attemptes.app.services.redis;
import com.attemptes.app.enums.AttemptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AttemptService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Duration ATTEMPT_WINDOW = Duration.ofMinutes(1);
    private static final Duration BLOCK_DURATION = Duration.ofHours(24);

    public boolean checkAttempts(AttemptType attemptType, String identifier) {
        String key = "attempts:" + attemptType.getKeyPrefix() + ":" + identifier;
        Long attempts = incrementAttempts(key, ATTEMPT_WINDOW);
        return attempts <= attemptType.getMaxAttempts();
    }

    private Long incrementAttempts(String key, Duration window) {
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts == 1) {
            redisTemplate.expire(key, window);
        }
        return attempts;
    }

    public void block(AttemptType attemptType, String identifier) {
        String key = "block:" + attemptType.getKeyPrefix() + ":" + identifier;
        redisTemplate.opsForValue().set(key, "blocked", BLOCK_DURATION);
    }

    public boolean isBlocked(AttemptType attemptType, String identifier) {
        String key = "block:" + attemptType.getKeyPrefix() + ":" + identifier;
        return redisTemplate.opsForValue().get(key) != null;
    }

    public void unblock(AttemptType attemptType, String identifier) {
        String key = "block:" + attemptType.getKeyPrefix() + ":" + identifier;
        redisTemplate.delete(key);
    }
}