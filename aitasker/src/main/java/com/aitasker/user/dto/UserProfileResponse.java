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
    private String email;
    private Role role;
    private UserStatus status;
}
