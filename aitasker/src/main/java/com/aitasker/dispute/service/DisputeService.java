package com.aitasker.dispute.service;

import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.payment.enums.PaymentStatus;
import com.aitasker.dispute.dto.request.CreateDisputeRequest;
import com.aitasker.dispute.dto.request.ResolveDisputeRequest;
import com.aitasker.dispute.dto.response.DisputeResponse;
import com.aitasker.dispute.entity.Dispute;
import com.aitasker.dispute.repository.DisputeRepository;
import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.milestone.repository.MilestoneRepository;
import com.aitasker.notification.service.NotificationService;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.payment.service.PaymentService;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public DisputeResponse create(CreateDisputeRequest request, User creator) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project không tìm thấy"));

        boolean isParticipant = project.getClient().getId().equals(creator.getId())
                || project.getExpert().getId().equals(creator.getId());
        if (!isParticipant) {
            throw new ForbiddenException("Bạn không thuộc project này");
        }

        Milestone milestone = null;
        if (request.getMilestoneId() != null) {
            milestone = milestoneRepository.findById(request.getMilestoneId())
                    .orElseThrow(() -> new ResourceNotFoundException("Milestone không tìm thấy"));
        }

        Dispute dispute = Dispute.builder()
                .project(project)
                .milestone(milestone)
                .creator(creator)
                .reason(request.getReason())
                .status(DisputeStatus.OPEN)
                .build();
        disputeRepository.save(dispute);

        project.setStatus(ProjectStatus.DISPUTED);
        projectRepository.save(project);

        Long otherPartyId = project.getClient().getId().equals(creator.getId())
                ? project.getExpert().getId()
                : project.getClient().getId();
        notificationService.createNotification(
                otherPartyId,
                "Có tranh chấp mới",
                creator.getName() + " đã mở tranh chấp cho dự án #" + project.getId() + ".",
                "DISPUTE_OPENED"
        );
        auditLogService.log(creator, "DISPUTE_CREATED", "Dispute #" + dispute.getId() + " cho Project #" + project.getId());

        return DisputeResponse.from(dispute);
    }

    @Transactional(readOnly = true)
    public List<DisputeResponse> getAll(DisputeStatus status) {
        List<Dispute> disputes = status != null
                ? disputeRepository.findByStatus(status)
                : disputeRepository.findAll();
        return disputes.stream().map(DisputeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DisputeResponse getDetail(Long disputeId, Long currentUserId, boolean isAdmin) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute không tìm thấy"));

        boolean isCreator = dispute.getCreator() != null && dispute.getCreator().getId().equals(currentUserId);
        boolean isClient = dispute.getProject() != null && dispute.getProject().getClient() != null
                && dispute.getProject().getClient().getId().equals(currentUserId);
        boolean isExpert = dispute.getProject() != null && dispute.getProject().getExpert() != null
                && dispute.getProject().getExpert().getId().equals(currentUserId);
        if (!isAdmin && !isCreator && !isClient && !isExpert) {
            throw new ForbiddenException("Bạn không có quyền xem dispute này.");
        }

        return DisputeResponse.from(dispute);
    }

    public DisputeResponse resolve(Long id, ResolveDisputeRequest request) {
        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute không tìm thấy"));

        if (dispute.getStatus() == DisputeStatus.RESOLVED_REFUND
                || dispute.getStatus() == DisputeStatus.RESOLVED_REJECTED) {
            throw new BadRequestException("Dispute đã được xử lý");
        }

        if (request.getStatus() != DisputeStatus.RESOLVED_REFUND
                && request.getStatus() != DisputeStatus.RESOLVED_REJECTED) {
            throw new BadRequestException("Kết quả xử lý phải là RESOLVED_REFUND hoặc RESOLVED_REJECTED");
        }

        Project project = dispute.getProject();

        if (request.getStatus() == DisputeStatus.RESOLVED_REFUND) {
            List<Payment> heldPayments = paymentRepository.findByProjectIdAndStatus(
                    project.getId(), PaymentStatus.HELD);
            for (Payment payment : heldPayments) {
                paymentService.refund(payment.getId(), "Dispute #" + dispute.getId() + " resolved");
            }
            project.setStatus(ProjectStatus.CANCELLED);
        } else {
            project.setStatus(ProjectStatus.ACTIVE);
        }
        projectRepository.save(project);

        dispute.setStatus(request.getStatus());
        dispute.setResolution(request.getResolution());
        disputeRepository.save(dispute);

        String result = request.getStatus() == DisputeStatus.RESOLVED_REFUND ? "hoàn tiền" : "từ chối";
        notificationService.createNotification(
                dispute.getCreator().getId(),
                "Tranh chấp đã được xử lý",
                "Tranh chấp #" + dispute.getId() + " đã được xử lý: " + result + ". Lý do: " + request.getResolution(),
                "DISPUTE_RESOLVED"
        );
        auditLogService.log(null, "DISPUTE_RESOLVED", "Dispute #" + dispute.getId() + " -> " + request.getStatus());

        return DisputeResponse.from(dispute);
    }
}
