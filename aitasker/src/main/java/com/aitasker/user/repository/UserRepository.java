// UserRepository.java
package com.aitasker.user.repository;

import com.aitasker.common.enums.Role;
import com.aitasker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRole(Role role);
    boolean existsByRole(Role role);
}