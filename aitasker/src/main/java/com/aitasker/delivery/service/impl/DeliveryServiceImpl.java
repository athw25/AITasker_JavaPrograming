package com.aitasker.delivery.service.impl;

import com.aitasker.common.enums.DeliveryStatus;
import com.aitasker.common.enums.MilestoneStatus;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.delivery.dto.request.SubmitDeliveryRequest;
import com.aitasker.delivery.dto.response.DeliveryResponse;
import com.aitasker.delivery.entity.Delivery;
import com.aitasker.delivery.mapper.DeliveryMapper;
import com.aitasker.delivery.repository.DeliveryRepository;
import com.aitasker.delivery.service.DeliveryService;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.exception.InvalidMilestoneStateException;
import com.aitasker.milestone.exception.MilestoneNotFoundException;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final MilestoneRepository milestoneRepository;
    private final DeliveryMapper deliveryMapper;

    @Override
    @Transactional
    public DeliveryResponse submitDelivery(SubmitDeliveryRequest request, User currentUser) {
        Milestone milestone = milestoneRepository.findByIdForUpdate(request.getMilestoneId())
                .orElseThrow(() -> new MilestoneNotFoundException(request.getMilestoneId()));
        if (!Objects.equals(milestone.getProject().getExpert().getId(), currentUser.getId())) {
            throw new ForbiddenException("Only the assigned expert can submit this milestone");
        }
        if (milestone.getProject().getStatus() != ProjectStatus.ACTIVE) {
            throw new InvalidMilestoneStateException("Deliveries can only be submitted to an active project");
        }
        if (milestone.getStatus() != MilestoneStatus.PENDING
                && milestone.getStatus() != MilestoneStatus.REJECTED) {
            throw new InvalidMilestoneStateException("Milestone must be pending or rejected before submission");
        }

        int nextVersion = deliveryRepository.findTopByMilestoneIdOrderByVersionDesc(milestone.getId())
                .map(Delivery::getVersion).orElse(0) + 1;
        LocalDateTime now = LocalDateTime.now();
        Delivery delivery = Delivery.builder()
                .milestone(milestone)
                .fileUrl(request.getFileUrl().trim())
                .note(request.getNote())
                .version(nextVersion)
                .submittedBy(currentUser)
                .submittedAt(now)
                .status(DeliveryStatus.SUBMITTED)
                .build();
        milestone.setStatus(MilestoneStatus.SUBMITTED);
        milestone.setSubmittedAt(now);
        milestone.addDelivery(delivery);
        return deliveryMapper.toResponse(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryResponse> getMilestoneDeliveries(Long milestoneId, User currentUser) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new MilestoneNotFoundException(milestoneId));
        boolean participant = Objects.equals(milestone.getProject().getClient().getId(), currentUser.getId())
                || Objects.equals(milestone.getProject().getExpert().getId(), currentUser.getId());
        if (!participant && currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenException("You are not a participant in this project");
        }
        return deliveryRepository.findByMilestoneIdOrderByVersionAsc(milestoneId).stream()
                .map(deliveryMapper::toResponse).toList();
    }
}
