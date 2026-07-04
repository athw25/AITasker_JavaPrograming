package com.aitasker.admin.dto;

import com.aitasker.common.enums.Role;
import com.aitasker.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Dữ liệu user trả về cho Admin — KHÔNG bao giờ chứa password.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
