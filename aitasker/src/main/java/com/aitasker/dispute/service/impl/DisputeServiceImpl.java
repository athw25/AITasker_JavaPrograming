package com.aitasker.dispute.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.common.response.PageResponse;
import com.aitasker.dispute.dto.request.AddDisputeMessageRequest;
import com.aitasker.dispute.dto.request.AddEvidenceRequest;
import com.aitasker.dispute.dto.request.CreateDisputeRequest;
import com.aitasker.dispute.dto.request.DisputeResolveRequest;
import com.aitasker.dispute.dto.response.DisputeResponse;
import com.aitasker.dispute.entity.Dispute;
import com.aitasker.dispute.entity.DisputeEvidence;
import com.aitasker.dispute.entity.DisputeMessage;
import com.aitasker.dispute.repository.DisputeRepository;
import com.aitasker.dispute.service.DisputeService;
import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.BusinessException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.payment.service.PaymentService;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;
    private final ProjectRepository projectRepository;
    private final PaymentService paymentService;
    private final AnalyticsService analyticsService;
// import: import com.aitasker.payment.service.PaymentService;

    private static final List<DisputeStatus> OPEN_STATES =
            List.of(DisputeStatus.OPEN, DisputeStatus.IN_REVIEW);

    @Override
    @Transactional
    public DisputeResponse createDispute(CreateDisputeRequest request, User currentUser) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy Project với id: " + request.getProjectId()));

        boolean isParticipant = project.getClient().getId().equals(currentUser.getId())
                || project.getExpert().getId().equals(currentUser.getId());
        if (!isParticipant) {
            throw new ForbiddenException("Bạn không thuộc Project này nên không thể tạo Dispute");
        }

        boolean hasOpenDispute = disputeRepository
                .existsByProjectIdAndStatusIn(project.getId(), OPEN_STATES);
        if (hasOpenDispute) {
            throw new BusinessException("Project này đang có một Dispute chưa được xử lý xong");
        }

        Dispute dispute = Dispute.builder()
                .project(project)
                .createdBy(currentUser)
                .reason(request.getReason())
                .status(DisputeStatus.OPEN)
                .build();

        Dispute saved = disputeRepository.save(dispute);
        analyticsService.recordEvent(AnalyticsEventType.DISPUTE_CREATED, currentUser.getId(),
                currentUser.getRole().name(), "DISPUTE", String.valueOf(saved.getId()));
        return toResponse(saved);
    }

    @Override
    public DisputeResponse getDisputeById(Long id, User currentUser) {
        Dispute dispute = getDisputeOrThrow(id);
        checkViewPermission(dispute, currentUser);
        return toResponse(dispute);
    }

    @Override
    public PageResponse<DisputeResponse> getDisputes(String status, int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Dispute> result;
        if (status != null && !status.isBlank()) {
            DisputeStatus disputeStatus;
            try {
                disputeStatus = DisputeStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("status không hợp lệ: " + status);
            }
            result = disputeRepository.findByStatus(disputeStatus, pageable);
        } else {
            result = disputeRepository.findAll(pageable);
        }

        // ADMIN xem tất cả; CLIENT/EXPERT chỉ xem dispute liên quan tới mình
        List<DisputeResponse> content = result.getContent().stream()
                .filter(d -> isAdmin(currentUser) || isParticipant(d, currentUser))
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @Override
    @Transactional
    public DisputeResponse resolveDispute(Long id, DisputeResolveRequest request, User currentUser) {
        if (!isAdmin(currentUser)) {
            throw new ForbiddenException("Chỉ Admin mới được xử lý Dispute");
        }

        Dispute dispute = getDisputeOrThrow(id);

        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new BusinessException("Dispute này đã được xử lý xong, không thể xử lý lại");
        }

        if (request.getStatus() != DisputeStatus.RESOLVED && request.getStatus() != DisputeStatus.REJECTED) {
            throw new BadRequestException("status chỉ được phép là RESOLVED hoặc REJECTED");
        }

        if (request.getStatus() == DisputeStatus.RESOLVED
                && request.getRefundAmount() != null
                && request.getRefundAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            if (request.getPaymentId() == null) {
                throw new BadRequestException("Cần truyền paymentId khi có refundAmount");
            }
            paymentService.refund(
                request.getPaymentId(),
                request.getRefundAmount(),
                "Hoàn tiền do Dispute #" + id + ": " + request.getResolution()
            );
}

        dispute.setStatus(request.getStatus());
        dispute.setResolution(request.getResolution());
        dispute.setResolvedAt(LocalDateTime.now());

        Dispute resolved = disputeRepository.save(dispute);
        analyticsService.recordEvent(AnalyticsEventType.DISPUTE_RESOLVED, currentUser.getId(),
                "ADMIN", "DISPUTE", String.valueOf(id));
        return toResponse(resolved);
    }

    @Override
    @Transactional
    public DisputeResponse addMessage(Long id, AddDisputeMessageRequest request, User currentUser) {
        Dispute dispute = getDisputeOrThrow(id);
        checkViewPermission(dispute, currentUser);

        if (!OPEN_STATES.contains(dispute.getStatus())) {
            throw new BusinessException("Chỉ được nhắn tin khi Dispute đang được xử lý");
        }

        DisputeMessage message = DisputeMessage.builder()
                .sender(currentUser)
                .message(request.getMessage())
                .build();

        dispute.addMessage(message);

        return toResponse(disputeRepository.save(dispute));
    }

    @Override
    @Transactional
    public DisputeResponse addEvidence(Long id, AddEvidenceRequest request, User currentUser) {
        Dispute dispute = getDisputeOrThrow(id);
        checkViewPermission(dispute, currentUser);

        if (dispute.getStatus() != DisputeStatus.OPEN) {
            throw new BusinessException("Chỉ được thêm Evidence khi Dispute đang ở trạng thái OPEN");
        }

        DisputeEvidence evidence = DisputeEvidence.builder()
                .fileUrl(request.getFileUrl())
                .description(request.getDescription())
                .build();

        dispute.addEvidence(evidence);

        return toResponse(disputeRepository.save(dispute));
    }

    // ===== Helper methods =====

    private Dispute getDisputeOrThrow(Long id) {
        return disputeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Dispute với id: " + id));
    }

    private boolean isAdmin(User user) {
        return user.getRole().name().equals("ADMIN");
    }

    private boolean isParticipant(Dispute dispute, User user) {
        Project project = dispute.getProject();
        return project.getClient().getId().equals(user.getId())
                || project.getExpert().getId().equals(user.getId());
    }

    private void checkViewPermission(Dispute dispute, User currentUser) {
        if (!isAdmin(currentUser) && !isParticipant(dispute, currentUser)) {
            throw new ForbiddenException("Bạn không có quyền xem Dispute này");
        }
    }

    private DisputeResponse toResponse(Dispute d) {
        return DisputeResponse.builder()
                .id(d.getId())
                .projectId(d.getProject().getId())
                .projectTitle(d.getProject().getJob() != null ? d.getProject().getJob().getTitle() : null)
                .createdById(d.getCreatedBy().getId())
                .createdByName(d.getCreatedBy().getName())
                .reason(d.getReason())
                .status(d.getStatus())
                .resolution(d.getResolution())
                .resolvedAt(d.getResolvedAt())
                .evidences(d.getEvidences().stream()
                        .map(e -> DisputeResponse.EvidenceItem.builder()
                                .id(e.getId())
                                .fileUrl(e.getFileUrl())
                                .description(e.getDescription())
                                .createdAt(e.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .messages(d.getMessages().stream()
                        .map(m -> DisputeResponse.MessageItem.builder()
                                .id(m.getId())
                                .senderId(m.getSender().getId())
                                .senderName(m.getSender().getName())
                                .message(m.getMessage())
                                .createdAt(m.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(d.getCreatedAt())
                .build();
    }
}