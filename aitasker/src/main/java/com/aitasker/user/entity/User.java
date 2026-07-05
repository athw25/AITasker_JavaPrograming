package com.aitasker.user.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.Role;
import com.aitasker.common.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Không bao giờ serialize password ra JSON, kể cả khi entity vô tình
    // bị trả trực tiếp từ một controller/service nào đó trong tương lai.
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;
    @Column(name = "failed_login_attempts", columnDefinition = "INT DEFAULT 0")
    private Integer failedLoginAttempts = 0;

    @Column(name = "account_locked", columnDefinition = "BIT DEFAULT 0")
    private Boolean accountLocked = false;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "remember_me_token", length = 500)
    private String rememberMeToken;

    @Column(name = "remember_me_expires")
    private LocalDateTime rememberMeExpires;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;
}