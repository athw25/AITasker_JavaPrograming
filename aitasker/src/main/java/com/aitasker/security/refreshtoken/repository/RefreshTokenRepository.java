package com.aitasker.security.refreshtoken.repository;

import com.aitasker.security.refreshtoken.entity.RefreshToken;
import com.aitasker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);

    @Modifying
    void deleteByUser(User user);

    @Modifying
    void deleteByToken(String token);
}
