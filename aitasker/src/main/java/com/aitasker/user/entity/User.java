package com.aitasker.user.entity;
import com.aitasker.common.enums.Role;
import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    private String fullName;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
}
