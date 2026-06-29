@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequest request, Principal principal) {
        User sender = getCurrentUser(principal);

        Message savedMessage = messageService.saveMessage(request, sender);

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
        // Tùy project bạn đang lưu user trong SecurityContext như nào
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}