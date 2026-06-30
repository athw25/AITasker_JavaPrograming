package com.aitasker.project.service.impl;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.exception.BusinessException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.project.dto.request.CreateProjectRequest;
import com.aitasker.project.dto.request.UpdateProjectRequest;
import com.aitasker.project.dto.response.ProjectDetailResponse;
import com.aitasker.project.dto.response.ProjectResponse;
import com.aitasker.project.entity.Project;
import com.aitasker.project.exception.InvalidProjectStateException;
import com.aitasker.project.exception.ProjectNotFoundException;
import com.aitasker.project.mapper.ProjectMapper;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.project.service.ProjectService;
import com.aitasker.proposal.entity.Proposal;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProposalRepository proposalRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, User currentUser) {
        Proposal proposal = proposalRepository.findById(request.getProposalId())
                .orElseThrow(() -> new BusinessException("Proposal not found with id: " + request.getProposalId()));
        if (proposal.getStatus() != ProposalStatus.ACCEPTED) {
            throw new InvalidProjectStateException("Only an accepted proposal can create a project");
        }
        if (proposal.getJob() == null || proposal.getExpert() == null || proposal.getJob().getClient() == null) {
            throw new BusinessException("Proposal is missing its job, client, or expert relationship");
        }
        if (!Objects.equals(proposal.getJob().getClient().getId(), currentUser.getId())) {
            throw new ForbiddenException("Only the job owner can create this project");
        }
        if (projectRepository.existsByProposalId(proposal.getId())) {
            throw new InvalidProjectStateException("This proposal already has a project");
        }
        if (projectRepository.existsByJobId(proposal.getJob().getId())) {
            throw new InvalidProjectStateException("This job already has a project");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BusinessException("Project end date must not be before start date");
        }

        Project project = Project.builder()
                .client(proposal.getJob().getClient())
                .expert(proposal.getExpert())
                .job(proposal.getJob())
                .proposal(proposal)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ProjectStatus.ACTIVE)
                .build();
        try {
            return projectMapper.toResponse(projectRepository.saveAndFlush(project));
        } catch (DataIntegrityViolationException exception) {
            throw new InvalidProjectStateException("The proposal or job already has a project");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProject(Long id, User currentUser) {
        Project project = findProject(id);
        assertParticipantOrAdmin(project, currentUser);
        return projectMapper.toDetailResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(User currentUser) {
        return projectRepository.findDistinctByClientIdOrExpertIdOrderByCreatedAtDesc(
                        currentUser.getId(), currentUser.getId()).stream()
                .map(projectMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getClientProjects(User currentUser) {
        return projectRepository.findByClientId(currentUser.getId()).stream()
                .map(projectMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getExpertProjects(User currentUser) {
        return projectRepository.findByExpertId(currentUser.getId()).stream()
                .map(projectMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request, User currentUser) {
        Project project = findProject(id);
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only an administrator can update project lifecycle data directly");
        }
        if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());
        if (request.getStatus() != null) project.setStatus(request.getStatus());
        if (project.getStartDate() != null && project.getEndDate() != null
                && project.getEndDate().isBefore(project.getStartDate())) {
            throw new BusinessException("Project end date must not be before start date");
        }
        return projectMapper.toResponse(project);
    }

    private Project findProject(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
    }

    private void assertParticipantOrAdmin(Project project, User user) {
        boolean participant = Objects.equals(project.getClient().getId(), user.getId())
                || Objects.equals(project.getExpert().getId(), user.getId());
        if (!participant && user.getRole() != Role.ADMIN) {
            throw new ForbiddenException("You are not a participant in this project");
        }
    }
}
