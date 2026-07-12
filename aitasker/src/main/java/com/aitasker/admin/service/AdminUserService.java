package com.aitasker.admin.service;

import com.aitasker.common.enums.Role;
import com.aitasker.common.enums.UserStatus;
import com.aitasker.common.response.PageResponse;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.user.entity.User;
import com.aitasker.admin.dto.UserSummaryResponse;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<UserSummaryResponse> getAllUsers(String keyword, Role role, int page, int size) {
        Page<User> userPage = userRepository.search(
                (keyword == null || keyword.isBlank()) ? null : keyword.trim(),
                role,
                PageRequest.of(page, size));

        return new PageResponse<>(
                userPage.getContent().stream().map(UserSummaryResponse::from).toList(),
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast()
        );
    }
    public void banUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(UserStatus.BANNED);
        userRepository.save(user);
    }
    public void unbanUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }
}