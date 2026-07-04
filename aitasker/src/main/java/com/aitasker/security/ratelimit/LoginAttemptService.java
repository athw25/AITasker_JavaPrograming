package com.aitasker.security.ratelimit;

import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {

    private static final int MAX_ATTEMPT = 5;
    private static final long LOCK_TIME_DURATION = 15; // minutes

    private final UserRepository userRepository;

    @Transactional
    public void loginSucceeded(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFailedLoginAttempts(0);
            user.setAccountLocked(false);
            user.setLockedUntil(null);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            log.info("Login succeeded for user: {}", email);
        }
    }

    @Transactional
    public void loginFailed(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            int attempts = user.getFailedLoginAttempts() == null ? 1 : user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_ATTEMPT) {
                user.setAccountLocked(true);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_DURATION));
                log.warn("Account locked for user: {} after {} attempts", email, attempts);
            }
            userRepository.save(user);
        }
    }

    public boolean isAccountLocked(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (Boolean.TRUE.equals(user.getAccountLocked())) {
                if (user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
                    unlockAccount(email);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void unlockAccount(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
            log.info("Account unlocked for user: {}", email);
        }
    }

    public int getFailedAttempts(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(user -> user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts())
                .orElse(0);
    }
}
