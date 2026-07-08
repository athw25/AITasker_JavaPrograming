package com.aitasker.config;

import com.aitasker.common.enums.Role;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Tạo tài khoản ADMIN đầu tiên khi khởi động app, vì API
 * POST /api/auth/register giờ đã CHẶN việc tự đăng ký role ADMIN
 * (trước đây bị lỗi cho phép, đã vá ở AuthServiceImpl).
 *
 * Chỉ chạy khi hệ thống CHƯA có Admin nào (an toàn khi restart nhiều lần,
 * không tạo trùng). Cấu hình qua application.yaml / biến môi trường:
 *
 *   app.admin.email=admin@aitasker.com
 *   app.admin.password=ChangeMe123!
 *
 * Đổi mật khẩu mặc định trước khi dùng thật, kể cả trong môi trường học tập.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@aitasker.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        boolean adminExists = userRepository.existsByRole(Role.ADMIN);

        if (adminExists) {
            return;
        }

        User admin = new User();
        admin.setName("System Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        log.info("Đã tạo tài khoản ADMIN mặc định: {} (nhớ đổi mật khẩu!)", adminEmail);
    }
}
