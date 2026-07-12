package com.aitasker.message.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.message.dto.MessageResponse;
import com.aitasker.message.service.MessageService;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/project/{projectId}")
    public ApiResponse<List<MessageResponse>> getProjectMessages(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        return ApiResponse.success(messageService.getMessagesByProject(projectId, currentUser));
    }

    private User getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }

        return (User) principal;
    }
}
