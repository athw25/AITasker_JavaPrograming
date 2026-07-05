package com.aitasker.message.controller;

import com.aitasker.message.dto.ChatMessageRequest;
import com.aitasker.message.dto.MessageResponse;
import com.aitasker.message.service.MessageService;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request, Principal principal) {
        User sender = getCurrentUser(principal);

        MessageResponse savedMessage = messageService.saveMessage(request, sender);

        messagingTemplate.convertAndSend(
                "/topic/projects/" + request.getProjectId(),
                savedMessage
        );

        messagingTemplate.convertAndSendToUser(
                String.valueOf(request.getReceiverId()),
                "/queue/messages",
                savedMessage
        );
    }

    private User getCurrentUser(Principal principal) {
        Object authPrincipal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (authPrincipal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }

        return (User) authPrincipal;
    }
}
