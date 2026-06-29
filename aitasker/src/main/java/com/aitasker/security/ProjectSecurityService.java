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
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (hasRole(user, "ADMIN")) {
            return;
        }

        if (hasRole(user, "CLIENT")
                && user.getId().equals(project.getClientId())) {
            return;
        }

        if (hasRole(user, "AI_EXPERT")
                && user.getId().equals(project.getExpertId())) {
            return;
        }

        throw new AccessDeniedException("You cannot access this project");
    }

    private boolean hasRole(User user, String role) {
        return Role.valueOf(role).equals(user.getRole());
    }
}
