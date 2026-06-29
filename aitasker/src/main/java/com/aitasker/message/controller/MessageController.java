@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Message>> getProjectMessages(
            @PathVariable Long projectId,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                messageService.getMessagesByProject(projectId, currentUser)
        );
    }
}