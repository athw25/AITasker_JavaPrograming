package com.aitasker.admin.service;

import com.aitasker.common.enums.UserStatus;
import com.aitasker.exception.ResourceNotFoundException;
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

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserSummaryResponse::from)
                .toList();
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