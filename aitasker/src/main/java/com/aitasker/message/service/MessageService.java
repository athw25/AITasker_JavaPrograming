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