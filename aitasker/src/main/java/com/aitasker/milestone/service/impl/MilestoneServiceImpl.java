package com.aitasker.milestone.service.impl;

import com.aitasker.common.enums.DeliveryStatus;
import com.aitasker.common.enums.MilestoneStatus;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.delivery.dto.request.SubmitDeliveryRequest;
import com.aitasker.delivery.entity.Delivery;
import com.aitasker.delivery.repository.DeliveryRepository;
import com.aitasker.delivery.service.DeliveryService;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.milestone.dto.request.CreateMilestoneRequest;
import com.aitasker.milestone.dto.request.SubmitMilestoneRequest;
import com.aitasker.milestone.dto.request.UpdateMilestoneRequest;
import com.aitasker.milestone.dto.response.MilestoneResponse;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.exception.InvalidMilestoneStateException;
import com.aitasker.milestone.exception.MilestoneNotFoundException;
import com.aitasker.milestone.mapper.MilestoneMapper;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.milestone.service.MilestoneService;
import com.aitasker.project.entity.Project;
import com.aitasker.project.exception.InvalidProjectStateException;
import com.aitasker.project.exception.ProjectNotFoundException;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MilestoneServiceImpl implements MilestoneService {
    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;
    private final MilestoneMapper milestoneMapper;

    @Override
    @Transactional
    public MilestoneResponse createMilestone(CreateMilestoneRequest request, User currentUser) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(request.getProjectId()));
        assertClient(project, currentUser);
        assertActive(project);
        Milestone milestone = Milestone.builder()
                .project(project)
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .status(MilestoneStatus.PENDING)
                .build();
        project.addMilestone(milestone);
        return milestoneMapper.toResponse(milestoneRepository.save(milestone));
    }

    @Override
    @Transactional
    public MilestoneResponse updateMilestone(Long id, UpdateMilestoneRequest request, User currentUser) {
        Milestone milestone = findLocked(id);
        assertClient(milestone.getProject(), currentUser);
        assertActive(milestone.getProject());
        if (milestone.getStatus() != MilestoneStatus.PENDING
                && milestone.getStatus() != MilestoneStatus.REJECTED) {
            throw new InvalidMilestoneStateException("Only pending or rejected milestones can be updated");
        }
        if (request.getTitle() != null) {
            if (request.getTitle().isBlank()) throw new IllegalArgumentException("Title must not be blank");
            milestone.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) milestone.setDescription(request.getDescription());
        if (request.getAmount() != null) milestone.setAmount(request.getAmount());
        if (request.getDueDate() != null) milestone.setDueDate(request.getDueDate());
        return milestoneMapper.toResponse(milestone);
    }

    @Override
    @Transactional
    public MilestoneResponse submitMilestone(Long id, SubmitMilestoneRequest request, User currentUser) {
        deliveryService.submitDelivery(SubmitDeliveryRequest.builder()
                .milestoneId(id).fileUrl(request.getFileUrl()).note(request.getNote()).build(), currentUser);
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new MilestoneNotFoundException(id));
        return milestoneMapper.toResponse(milestone);
    }

    @Override
    @Transactional
    public MilestoneResponse approveMilestone(Long id, User currentUser) {
        Milestone milestone = findLocked(id);
        assertClient(milestone.getProject(), currentUser);
        assertActive(milestone.getProject());
        if (milestone.getStatus() != MilestoneStatus.SUBMITTED) {
            throw new InvalidMilestoneStateException("Only a submitted milestone can be approved");
        }
        Delivery delivery = latestDelivery(milestone);
        LocalDateTime now = LocalDateTime.now();
        delivery.setStatus(DeliveryStatus.APPROVED);
        delivery.setApprovedAt(now);
        delivery.setRejectReason(null);
        milestone.setStatus(MilestoneStatus.APPROVED);
        milestone.setApprovedAt(now);
        return milestoneMapper.toResponse(milestone);
    }

    @Override
    @Transactional
    public MilestoneResponse rejectMilestone(Long id, String reason, User currentUser) {
        Milestone milestone = findLocked(id);
        assertClient(milestone.getProject(), currentUser);
        assertActive(milestone.getProject());
        if (milestone.getStatus() != MilestoneStatus.SUBMITTED) {
            throw new InvalidMilestoneStateException("Only a submitted milestone can be rejected");
        }
        Delivery delivery = latestDelivery(milestone);
        delivery.setStatus(DeliveryStatus.REJECTED);
        delivery.setRejectReason(reason.trim());
        milestone.setStatus(MilestoneStatus.REJECTED);
        milestone.setApprovedAt(null);
        return milestoneMapper.toResponse(milestone);
    }

    @Override
    @Transactional
    public MilestoneResponse releasePayment(Long id, User currentUser) {
        Milestone milestone = findLocked(id);
        Project project = milestone.getProject();
        assertClient(project, currentUser);
        assertActive(project);
        if (milestone.getStatus() != MilestoneStatus.APPROVED) {
            throw new InvalidMilestoneStateException("Only an approved milestone can be paid");
        }
        // TODO Integrate the payment provider/escrow transaction before marking the milestone paid.
        milestone.setStatus(MilestoneStatus.PAID);
        boolean allPaid = !project.getMilestones().isEmpty()
                && project.getMilestones().stream().allMatch(item -> item.getStatus() == MilestoneStatus.PAID);
        if (allPaid) project.setStatus(ProjectStatus.COMPLETED);
        return milestoneMapper.toResponse(milestone);
    }

    private Milestone findLocked(Long id) {
        return milestoneRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new MilestoneNotFoundException(id));
    }

    private Delivery latestDelivery(Milestone milestone) {
        return deliveryRepository.findTopByMilestoneIdOrderByVersionDesc(milestone.getId())
                .orElseThrow(() -> new InvalidMilestoneStateException("Milestone has no delivery to review"));
    }

    private void assertClient(Project project, User user) {
        if (!Objects.equals(project.getClient().getId(), user.getId())) {
            throw new ForbiddenException("Only the project client can perform this action");
        }
    }

    private void assertActive(Project project) {
        if (project.getStatus() != ProjectStatus.ACTIVE) {
            throw new InvalidProjectStateException("Milestones can only be changed in an active project");
        }
    }
}
