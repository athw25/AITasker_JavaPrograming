@Service
@RequiredArgsConstructor
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;

    public void checkCanAccessProject(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (hasRole(user, "ADMIN")) {
            return;
        }

        if (hasRole(user, "CLIENT")
                && project.getClient().getId().equals(user.getId())) {
            return;
        }

        if (hasRole(user, "EXPERT")
                && project.getExpert() != null
                && project.getExpert().getId().equals(user.getId())) {
            return;
        }

        throw new AccessDeniedException("You cannot access this project");
    }

    private boolean hasRole(User user, String role) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}