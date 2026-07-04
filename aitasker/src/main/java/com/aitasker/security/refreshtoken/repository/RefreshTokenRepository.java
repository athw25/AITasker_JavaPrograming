package com.aitasker.security.refreshtoken.repository;

import com.aitasker.security.refreshtoken.entity.RefreshToken;
import com.aitasker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUser(User user);
    List<RefreshToken> findByUserAndRevokedFalse(User user);
    void deleteByUser(User user);
    void deleteByUserAndRevokedTrue(User user);
}
