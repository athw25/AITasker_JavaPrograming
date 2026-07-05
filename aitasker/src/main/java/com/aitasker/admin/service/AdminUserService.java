package com.aitasker.admin.service;

import com.aitasker.admin.dto.UserSummaryResponse;
import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.enums.UserStatus;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserSummaryResponse::from)
                .toList();
    }

    public UserSummaryResponse banUser(Long id) {
        User user = findUser(id);
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
        auditLogService.log(user, "ADMIN_BAN_USER", "Ban user #" + user.getId());
        return UserSummaryResponse.from(user);
    }

    public UserSummaryResponse unbanUser(Long id) {
        User user = findUser(id);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        auditLogService.log(user, "ADMIN_UNBAN_USER", "Unban user #" + user.getId());
        return UserSummaryResponse.from(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user"));
    }
}
