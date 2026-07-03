package com.aitasker.message.service;

import com.aitasker.message.dto.ChatMessageRequest;
import com.aitasker.message.entity.Message;
import com.aitasker.message.repository.MessageRepository;
import com.aitasker.security.ProjectSecurityService;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ProjectSecurityService projectSecurityService;

    public List<Message> getMessagesByProject(Long projectId, User currentUser) {
        projectSecurityService.checkCanAccessProject(projectId, currentUser);

        return messageRepository.findByProjectIdOrderBySentAtAsc(projectId);
    }

    public Message saveMessage(ChatMessageRequest request, User sender) {
        projectSecurityService.checkCanAccessProject(request.getProjectId(), sender);

        Message message = Message.builder()
                .projectId(request.getProjectId())
                .senderId(sender.getId())
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        return messageRepository.save(message);
    }
}
