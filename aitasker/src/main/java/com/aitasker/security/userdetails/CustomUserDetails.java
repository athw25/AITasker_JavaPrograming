// CustomUserDetails.java
package com.aitasker.security.userdetails;

import com.aitasker.common.enums.UserStatus;
import com.aitasker.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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

    /*
     * Trước bản vá này, hàm này luôn trả về true bất kể user.accountLocked
     * và user.lockedUntil trong Database — nghĩa là một tài khoản đang bị
     * khóa do đăng nhập sai quá số lần cho phép (Login Attempt Limiting)
     * vẫn tiếp tục dùng được các JWT đã cấp trước đó cho tới khi hết hạn.
     */
    @Override
    public boolean isAccountNonLocked() {
        if (!Boolean.TRUE.equals(user.getAccountLocked())) {
            return true;
        }
        // Đã hết thời gian khóa -> coi như không còn bị khóa (tự mở khóa mềm ở tầng đọc)
        return user.getLockedUntil() != null && user.getLockedUntil().isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /*
     * Trước bản vá này, hàm này luôn trả về true — Admin "ban" một User (đặt
     * status = BANNED) nhưng JWT cũ của người đó vẫn tiếp tục xác thực được
     * bình thường cho tới khi token tự hết hạn. Nay khóa theo đúng status.
     */
    @Override
    public boolean isEnabled() { return user.getStatus() == UserStatus.ACTIVE; }
}