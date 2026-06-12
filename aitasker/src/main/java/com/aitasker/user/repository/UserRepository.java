package com.aitasker.user.repository;

import com.aitasker.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    // Phuc An: thêm 2 hàm này để phục vụ logic Đăng ký và Đăng nhập
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}