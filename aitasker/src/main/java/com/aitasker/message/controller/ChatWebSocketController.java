package com.aitasker.message.controller;

import com.aitasker.message.dto.ChatMessageRequest;
import com.aitasker.message.dto.MessageResponse;
import com.aitasker.message.service.MessageService;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request, Principal principal) {
        User sender = getCurrentUser(principal);

        MessageResponse savedMessage = messageService.saveMessage(request, sender);

        messagingTemplate.convertAndSend(
                "/topic/projects/" + request.getProjectId(),
                savedMessage
        );

        // convertAndSendToUser định danh người nhận theo Principal.getName(), tức là
        // email (xem CustomUserDetails.getUsername()), không phải ID số của User.
        userRepository.findById(request.getReceiverId()).ifPresentOrElse(receiver ->
                messagingTemplate.convertAndSendToUser(
                        receiver.getEmail(),
                        "/queue/messages",
                        savedMessage
                ),
                () -> log.warn("Không tìm thấy người nhận với id: {}", request.getReceiverId())
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
