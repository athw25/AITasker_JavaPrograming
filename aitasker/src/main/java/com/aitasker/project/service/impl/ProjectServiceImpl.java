package com.aitasker.project.service.impl;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.project.dto.request.CreateProjectRequest;
import com.aitasker.project.dto.request.UpdateProjectRequest;
import com.aitasker.project.dto.response.ProjectDetailResponse;
import com.aitasker.project.dto.response.ProjectResponse;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.project.service.ProjectService;
import com.aitasker.proposal.entity.Proposal;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Override
    public ProjectResponse createProject(
            CreateProjectRequest request,
            User currentUser
    ) {
        throw new UnsupportedOperationException(
                "Manual project creation not implemented yet."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProject(
            Long id,
            User currentUser
    ) {
        throw new UnsupportedOperationException(
                "Not implemented yet."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(
            User currentUser
    ) {
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getClientProjects(
            User currentUser
    ) {
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getExpertProjects(
            User currentUser
    ) {
        return Collections.emptyList();
    }

    @Override
    public ProjectResponse updateProject(
            Long id,
            UpdateProjectRequest request,
            User currentUser
    ) {
        throw new UnsupportedOperationException(
                "Not implemented yet."
        );
    }

    /**
     * Được gọi khi Proposal được ACCEPT.
     */
    @Override
    public void createProjectFromProposal(
            Proposal proposal
    ) {

        if (proposal == null) {
            throw new IllegalArgumentException(
                    "Proposal cannot be null."
            );
        }

        if (projectRepository.existsByProposalId(
                proposal.getId()
        )) {
            return;
        }

        Project project = new Project();

        project.setClient(
                proposal.getJob().getClient()
        );

        project.setExpert(
                proposal.getExpert()
        );

        project.setJob(
                proposal.getJob()
        );

        project.setProposal(
                proposal
        );

        project.setStartDate(
                LocalDate.now()
        );

        project.setEndDate(
                proposal.getJob().getDeadline()
        );

        project.setStatus(
                ProjectStatus.ACTIVE
        );

        projectRepository.save(project);
    }
}