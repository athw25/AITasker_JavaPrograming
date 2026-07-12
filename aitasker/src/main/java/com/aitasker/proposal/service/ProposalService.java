// ProposalService.java
package com.aitasker.proposal.service;

import com.aitasker.common.enums.JobStatus;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.common.response.PageResponse;
import com.aitasker.exception.BusinessException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.notification.service.NotificationService;
import com.aitasker.project.service.ProjectService;
import com.aitasker.proposal.dto.request.ProposalRequestDTO;
import com.aitasker.proposal.dto.response.ProposalResponseDTO;
import com.aitasker.proposal.entity.Proposal;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;

    private final ProjectService projectService;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    // 1. CHUYÊN GIA NỘP ĐỀ XUẤT
    @Transactional
    public ProposalResponseDTO createProposal(ProposalRequestDTO request, Long expertId) {
        JobPost job = jobPostRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy công việc với ID: " + request.getJobId()));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new BusinessException("Công việc này không còn nhận đề xuất.");
        }

        User expert = userRepository.findById(expertId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + expertId));

        if (job.getClient().getId().equals(expertId)) {
            throw new ForbiddenException("Bạn không thể tự gửi đề xuất cho công việc do chính mình tạo.");
        }

        if (proposalRepository.existsByJobIdAndExpertId(job.getId(), expertId)) {
            throw new BusinessException("Bạn đã gửi đề xuất cho công việc này rồi.");
        }

        Proposal proposal = new Proposal();
        proposal.setJob(job);
        proposal.setExpert(expert);
        proposal.setBidAmount(request.getBidAmount());
        proposal.setCoverLetter(request.getCoverLetter());
        proposal.setDuration(request.getDuration());
        proposal.setStatus(ProposalStatus.PENDING);
        proposal.setSubmittedAt(LocalDateTime.now());

        proposal = proposalRepository.save(proposal);

        // Thông báo cho Client biết có đề xuất mới cho Job của họ
        notificationService.createNotification(
                job.getClient().getId(),
                "Có đề xuất mới",
                expert.getName() + " vừa gửi đề xuất cho công việc \"" + job.getTitle() + "\" của bạn.",
                "PROPOSAL_CREATED"
        );
        analyticsService.record("PROPOSAL_CREATED", proposal.getId(), null);

        return mapToDTO(proposal);
    }

    // 2. LẤY DANH SÁCH ĐỀ XUẤT THEO CÔNG VIỆC (CÓ PHÂN TRANG)
    public PageResponse<ProposalResponseDTO> getProposalsByJob(Long jobId, int page, int size) {
        Page<Proposal> proposalPage = proposalRepository.findByJobId(jobId, PageRequest.of(page, size));

        List<ProposalResponseDTO> content = proposalPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                proposalPage.getNumber(),
                proposalPage.getSize(),
                proposalPage.getTotalElements(),
                proposalPage.getTotalPages(),
                proposalPage.isFirst(),
                proposalPage.isLast()
        );
    }

    public List<ProposalResponseDTO> getMyProposals(Long expertId) {
        return proposalRepository.findByExpertId(expertId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProposalResponseDTO getProposalDetail(Long proposalId, Long currentUserId, boolean isAdmin) {
        Proposal proposal = getProposalById(proposalId);

        boolean isOwner = proposal.getExpert().getId().equals(currentUserId);
        boolean isJobOwner = proposal.getJob().getClient().getId().equals(currentUserId);
        if (!isAdmin && !isOwner && !isJobOwner) {
            throw new ForbiddenException("Bạn không có quyền xem đề xuất này.");
        }

        return mapToDTO(proposal);
    }

    // 3. KHÁCH HÀNG CHẤP NHẬN ĐỀ XUẤT
    @Transactional
    public void acceptProposal(Long proposalId, Long clientId) {
        Proposal proposal = getProposalById(proposalId);

        if (!proposal.getJob().getClient().getId().equals(clientId)) {
            throw new ForbiddenException("Chỉ người tạo công việc mới có quyền chấp nhận đề xuất.");
        }
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new BusinessException("Chỉ có thể chấp nhận đề xuất đang chờ duyệt.");
        }

        proposal.setStatus(ProposalStatus.ACCEPTED);
        proposal.getJob().setStatus(JobStatus.IN_PROGRESS);

        proposalRepository.save(proposal);
        jobPostRepository.save(proposal.getJob());

        projectService.createProjectFromProposal(proposal);

        // Thông báo cho Expert biết đề xuất của họ đã được chấp nhận
        notificationService.createNotification(
                proposal.getExpert().getId(),
                "Đề xuất được chấp nhận",
                "Đề xuất của bạn cho công việc \"" + proposal.getJob().getTitle() + "\" đã được chấp nhận.",
                "PROPOSAL_ACCEPTED"
        );
        analyticsService.record("PROPOSAL_ACCEPTED", proposal.getId(), null);
    }

    // 4. KHÁCH HÀNG TỪ CHỐI ĐỀ XUẤT
    @Transactional
    public void rejectProposal(Long proposalId, Long clientId) {
        Proposal proposal = getProposalById(proposalId);

        if (!proposal.getJob().getClient().getId().equals(clientId)) {
            throw new ForbiddenException("Chỉ người tạo công việc mới có quyền từ chối đề xuất.");
        }
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new BusinessException("Chỉ có thể từ chối đề xuất đang chờ duyệt.");
        }

        proposal.setStatus(ProposalStatus.REJECTED);
        proposalRepository.save(proposal);

        // Thông báo cho Expert biết đề xuất của họ bị từ chối
        notificationService.createNotification(
                proposal.getExpert().getId(),
                "Đề xuất bị từ chối",
                "Đề xuất của bạn cho công việc \"" + proposal.getJob().getTitle() + "\" đã bị từ chối.",
                "PROPOSAL_REJECTED"
        );
    }

    // 5. CHUYÊN GIA RÚT LẠI ĐỀ XUẤT
    @Transactional
    public void withdrawProposal(Long proposalId, Long expertId) {
        Proposal proposal = getProposalById(proposalId);

        if (!proposal.getExpert().getId().equals(expertId)) {
            throw new ForbiddenException("Bạn chỉ có thể rút lại đề xuất của chính mình.");
        }
        if (proposal.getStatus() != ProposalStatus.PENDING) {
            throw new BusinessException("Chỉ có thể rút lại đề xuất khi đang chờ duyệt.");
        }

        proposal.setStatus(ProposalStatus.WITHDRAWN);
        proposalRepository.save(proposal);

        // Thông báo cho Client biết Expert đã rút lại đề xuất
        notificationService.createNotification(
                proposal.getJob().getClient().getId(),
                "Đề xuất bị rút lại",
                proposal.getExpert().getName() + " đã rút lại đề xuất cho công việc \"" + proposal.getJob().getTitle() + "\".",
                "PROPOSAL_WITHDRAWN"
        );
    }

    // Hàm phụ trợ map Entity sang DTO
    private Proposal getProposalById(Long id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đề xuất với ID: " + id));
    }

    private ProposalResponseDTO mapToDTO(Proposal proposal) {
        return ProposalResponseDTO.builder()
                .id(proposal.getId())
                .jobId(proposal.getJob().getId())
                .jobTitle(proposal.getJob().getTitle())
                .expertId(proposal.getExpert().getId())
                .expertName(proposal.getExpert().getName())
                .bidAmount(proposal.getBidAmount())
                .coverLetter(proposal.getCoverLetter())
                .status(proposal.getStatus())
                .submittedAt(proposal.getSubmittedAt())
                .build();
    }
}