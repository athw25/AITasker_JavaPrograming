// CustomUserDetails.java
package com.aitasker.security.userdetails;

import com.aitasker.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Phuc An bổ sung logic Wrapper: Bọc Entity User thành UserDetails cho Spring Security
public class CustomUserDetails implements UserDetails {

    private final User user;

    // Hàm khởi tạo nhận vào User từ Database
    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Hàm để lấy lại thông tin User gốc khi cần
    public User getUser() {
        return user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Hệ thống dùng Email để đăng nhập
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}