package com.aitasker.message.service;

import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.message.dto.ChatMessageRequest;
import com.aitasker.message.dto.MessageResponse;
import com.aitasker.message.entity.Message;
import com.aitasker.message.repository.MessageRepository;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.security.ProjectSecurityService;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectSecurityService projectSecurityService;

    public List<MessageResponse> getMessagesByProject(Long projectId, User currentUser) {
        projectSecurityService.checkCanAccessProject(projectId, currentUser);

        return messageRepository.findByProject_IdOrderBySentAtAsc(projectId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }

    public MessageResponse saveMessage(ChatMessageRequest request, User sender) {
        projectSecurityService.checkCanAccessProject(request.getProjectId(), sender);

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Project"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người nhận"));

        Message message = Message.builder()
                .project(project)
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        return MessageResponse.from(messageRepository.save(message));
    }
}
