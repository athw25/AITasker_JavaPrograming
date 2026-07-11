package com.aitasker.security.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_DURATION_MINUTES = 15;

    private final ConcurrentHashMap<String, AttemptRecord> attemptCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptCache.remove(key);
    }

    public void loginFailed(String key) {
        AttemptRecord record = attemptCache.get(key);
        if (record == null) {
            record = new AttemptRecord(1, null);
        } else {
            record.attempts += 1;
            if (record.attempts >= MAX_ATTEMPTS) {
                record.blockedUntil = LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES);
                log.warn("Key '{}' has failed login 5 times and is blocked until {}", key, record.blockedUntil);
            }
        }
        attemptCache.put(key, record);
    }

    public boolean isBlocked(String key) {
        AttemptRecord record = attemptCache.get(key);
        if (record == null) {
            return false;
        }
        if (record.blockedUntil != null) {
            if (LocalDateTime.now().isBefore(record.blockedUntil)) {
                return true;
            } else {
                attemptCache.remove(key);
            }
        }
        return false;
    }

    private static class AttemptRecord {
        int attempts;
        LocalDateTime blockedUntil;

        AttemptRecord(int attempts, LocalDateTime blockedUntil) {
            this.attempts = attempts;
            this.blockedUntil = blockedUntil;
        }
    }
}
