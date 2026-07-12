package com.aitasker.admin.service;

import com.aitasker.common.enums.UserStatus;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.security.audit.enums.AuditAction;
import com.aitasker.security.audit.service.AuditLogService;
import com.aitasker.security.token.RefreshTokenService;
import com.aitasker.user.entity.User;
import com.aitasker.admin.dto.UserSummaryResponse;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserSummaryResponse::from)
                .toList();
    }

    @Transactional
    public void banUser(Long id, Long adminId){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
        // Thu hồi Refresh Token ngay để user không thể tự làm mới access token sau khi bị ban
        refreshTokenService.revoke(user);
        auditLogService.log(AuditAction.ADMIN_USER_BANNED, adminId, null,
                "User", id.toString(), "Admin ban User #" + id);
    }

    @Transactional
    public void unbanUser(Long id, Long adminId){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        auditLogService.log(AuditAction.ADMIN_USER_UNBANNED, adminId, null,
                "User", id.toString(), "Admin unban User #" + id);
    }
}