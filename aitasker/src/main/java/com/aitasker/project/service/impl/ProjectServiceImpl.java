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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProposalRepository proposalRepository;
    private final ProjectMapper projectMapper;

    /**
     * Tạo Project thủ công.
     * Chỉ dùng trong trường hợp Admin hoặc mở rộng về sau.
     */
    @Override
    public ProjectResponse createProject(
            CreateProjectRequest request,
            User currentUser
    ) {

        Proposal proposal =
                proposalRepository.findById(
                                request.getProposalId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Proposal not found."
                                )
                        );

        if (proposal.getStatus() != ProposalStatus.ACCEPTED) {
            throw new InvalidProjectStateException(
                    "Only accepted proposal can create project."
            );
        }

        if (!proposal.getJob()
                .getClient()
                .getId()
                .equals(currentUser.getId())) {
            throw new ForbiddenException(
                    "Only job owner can create project."
            );
        }

        if (projectRepository.existsByProposalId(
                proposal.getId()
        )) {
            throw new InvalidProjectStateException(
                    "Project already exists."
            );
        }

        if (projectRepository.existsByJobId(
                proposal.getJob().getId()
        )) {
            throw new InvalidProjectStateException(
                    "This job already has a project."
            );
        }

        if (request.getEndDate()
                .isBefore(request.getStartDate())) {
            throw new BusinessException(
                    "End date cannot be before start date."
            );
        }

        Project project =
                Project.builder()
                        .client(
                                proposal.getJob().getClient()
                        )
                        .expert(
                                proposal.getExpert()
                        )
                        .job(
                                proposal.getJob()
                        )
                        .proposal(
                                proposal
                        )
                        .startDate(
                                request.getStartDate()
                        )
                        .endDate(
                                request.getEndDate()
                        )
                        .status(
                                ProjectStatus.ACTIVE
                        )
                        .build();

        try {
            project =
                    projectRepository.saveAndFlush(
                            project
                    );

            return projectMapper.toResponse(project);

        } catch (DataIntegrityViolationException ex) {
            throw new InvalidProjectStateException(
                    "Proposal already has a project."
            );
        }
    }

    /**
     * Được gọi tự động khi Proposal được ACCEPT.
     */
    public Project createProjectFromProposal(
            Proposal proposal
    ) {

        if (proposal == null) {
            throw new BusinessException(
                    "Proposal cannot be null."
            );
        }

        if (proposal.getStatus()
                != ProposalStatus.ACCEPTED) {
            throw new InvalidProjectStateException(
                    "Proposal must be ACCEPTED."
            );
        }

        if (projectRepository.existsByProposalId(
                proposal.getId()
        )) {
            throw new InvalidProjectStateException(
                    "Project already exists."
            );
        }

        Project project =
                Project.builder()
                        .client(
                                proposal.getJob().getClient()
                        )
                        .expert(
                                proposal.getExpert()
                        )
                        .job(
                                proposal.getJob()
                        )
                        .proposal(
                                proposal
                        )
                        .startDate(
                                LocalDate.now()
                        )
                        .endDate(
                                proposal.getJob()
                                        .getDeadline()
                        )
                        .status(
                                ProjectStatus.ACTIVE
                        )
                        .build();

        return projectRepository.save(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProject(
            Long id,
            User currentUser
    ) {

        Project project = findProject(id);

        assertParticipantOrAdmin(
                project,
                currentUser
        );

        return projectMapper.toDetailResponse(
                project
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(
            User currentUser
    ) {

        return projectRepository
                .findDistinctByClientIdOrExpertIdOrderByCreatedAtDesc(
                        currentUser.getId(),
                        currentUser.getId()
                )
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getClientProjects(
            User currentUser
    ) {

        return projectRepository
                .findByClientId(
                        currentUser.getId()
                )
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> getExpertProjects(
            User currentUser
    ) {

        return projectRepository
                .findByExpertId(
                        currentUser.getId()
                )
                .stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Override
    public ProjectResponse updateProject(
            Long id,
            UpdateProjectRequest request,
            User currentUser
    ) {

        Project project =
                findProject(id);

        if (currentUser.getRole()
                != Role.ADMIN) {
            throw new ForbiddenException(
                    "Only admin can update project."
            );
        }

        if (request.getStartDate() != null) {
            project.setStartDate(
                    request.getStartDate()
            );
        }

        if (request.getEndDate() != null) {
            project.setEndDate(
                    request.getEndDate()
            );
        }

        if (request.getStatus() != null) {
            project.setStatus(
                    request.getStatus()
            );
        }

        if (project.getStartDate() != null
                && project.getEndDate() != null
                && project.getEndDate()
                .isBefore(project.getStartDate())) {

            throw new BusinessException(
                    "End date cannot be before start date."
            );
        }

        project =
                projectRepository.save(project);

        return projectMapper.toResponse(
                project
        );
    }

    private Project findProject(
            Long id
    ) {

        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException(
                                id
                        )
                );
    }

    private void assertParticipantOrAdmin(
            Project project,
            User user
    ) {

        boolean participant =
                Objects.equals(
                        project.getClient().getId(),
                        user.getId()
                )
                        ||
                        Objects.equals(
                                project.getExpert().getId(),
                                user.getId()
                        );

        if (!participant
                && user.getRole()
                != Role.ADMIN) {

            throw new ForbiddenException(
                    "You are not allowed to access this project."
            );
        }
    }
}