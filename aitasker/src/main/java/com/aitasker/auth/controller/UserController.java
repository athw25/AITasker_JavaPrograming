package com.aitasker.auth.controller;

import com.aitasker.common.enums.Role;
import com.aitasker.common.enums.UserStatus;
import com.aitasker.common.response.ApiResponse;
import com.aitasker.exception.BadRequestException;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Getter
    @AllArgsConstructor
    public static class MeResponse {
        private Long id;
        private String name;
        private String email;
        private Role role;
        private UserStatus status;
    }

    @Getter
    @Setter
    public static class UpdateProfileRequest {
        @NotBlank(message = "Họ tên không được để trống")
        @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
        private String name;

        private String currentPassword;

        @Size(min = 6, max = 100, message = "Mật khẩu mới phải từ 6 ký tự")
        private String newPassword;
    }

    @GetMapping("/me")
    public ApiResponse<MeResponse> me(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success(toMeResponse(principal.getUser()));
    }

    @PutMapping("/me")
    public ApiResponse<MeResponse> updateMe(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        User user = principal.getUser();
        user.setName(request.getName());

        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (request.getCurrentPassword() == null
                    || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("Mật khẩu hiện tại không đúng");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        return ApiResponse.success("Cập nhật hồ sơ thành công", toMeResponse(user));
    }

    private MeResponse toMeResponse(User user) {
        return new MeResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getStatus());
    }
}
