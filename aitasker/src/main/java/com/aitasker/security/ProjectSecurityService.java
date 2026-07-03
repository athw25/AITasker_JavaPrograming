package com.aitasker.security;

import com.aitasker.common.enums.Role;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectSecurityService {

    private final ProjectRepository projectRepository;

    public void checkCanAccessProject(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new com.aitasker.exception.ResourceNotFoundException("Không tìm thấy Project"));

        if (hasRole(user, "ADMIN")) {
            return;
        }

        if (hasRole(user, "CLIENT")
                && user.getId().equals(project.getClient().getId() )) {
            return;
        }

        if (hasRole(user, "EXPERT")
                && user.getId().equals(project.getExpert().getId())) {
            return;
        }

        throw new AccessDeniedException("You cannot access this project");
    }

    private boolean hasRole(User user, String role) {
        return Role.valueOf(role).equals(user.getRole());
    }
}
