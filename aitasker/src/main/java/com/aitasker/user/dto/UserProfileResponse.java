package com.aitasker.user.dto;

import com.aitasker.common.enums.Role;
import com.aitasker.common.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String name; // Thêm name để tương thích với user.name của frontend
    private String email;
    private Role role;
    private UserStatus status;
}
