package com.aitasker.auth.repository;

import com.aitasker.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser_Id(Long userId);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiryDate < :now OR t.revoked = true")
    int deleteExpiredOrRevoked(@Param("now") LocalDateTime now);
}
