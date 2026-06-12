package com.aitasker.auth.service;

import com.aitasker.auth.dto.AuthResponse;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.dto.RegisterRequest;
import com.aitasker.common.enums.Role;
import com.aitasker.security.jwt.JwtService;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email này đã được sử dụng!");
        }

        User user = new User();
        user.setName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role không hợp lệ. Phải là CLIENT hoặc EXPERT");
        }

        userRepository.save(user);

        return AuthResponse.builder()
                .message("Đăng ký thành công! Vui lòng đăng nhập.")
                .token(null)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Tìm user trong Database dựa vào email
        com.aitasker.user.entity.User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng!"));

        // 2. Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng!");
        }

        // Gói Entity User (của TV2) vào trong CustomUserDetails (của TV3)
        com.aitasker.security.userdetails.CustomUserDetails userDetails = new com.aitasker.security.userdetails.CustomUserDetails(user);

        // 4. Sinh JWT Token
        String jwtToken = jwtService.generateToken(userDetails);

        // 5. Trả token về cho Client
        return AuthResponse.builder()
                .token(jwtToken)
                .message("Đăng nhập thành công!")
                .build();
    }
}