package com.aitasker.proposal.repository;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.common.enums.ProposalStatus;
import com.aitasker.proposal.entity.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProposalRepository
        extends JpaRepository<Proposal, Long> {

    /**
     * Kiểm tra Expert đã gửi Proposal cho Job hay chưa.
     */
    boolean existsByJobIdAndExpertId(
            Long jobId,
            Long expertId
    );

    /**
     * Lấy Proposal theo Job.
     */
    List<Proposal> findByJobId(
            Long jobId
    );

    /**
     * Lấy Proposal theo Job (phân trang).
     */
    Page<Proposal> findByJobId(
            Long jobId,
            Pageable pageable
    );

    /**
     * Lấy Proposal của Expert.
     */
    List<Proposal> findByExpertId(
            Long expertId
    );

    /**
     * Lấy Proposal của Expert theo trạng thái.
     */
    List<Proposal> findByExpertIdAndStatus(
            Long expertId,
            ProposalStatus status
    );

    /**
     * Tìm Proposal của Expert trong Job.
     */
    Optional<Proposal> findByJobIdAndExpertId(
            Long jobId,
            Long expertId
    );

    /**
     * Lấy Proposal theo trạng thái.
     */
    List<Proposal> findByStatus(
            ProposalStatus status
    );

    /**
     * Kiểm tra Job đã có Proposal được accept hay chưa.
     */
    boolean existsByJobIdAndStatus(
            Long jobId,
            ProposalStatus status
    );

    /**
     * Đếm số Proposal của Job.
     */
    long countByJobId(
            Long jobId
    );

    /**
     * Đếm số Proposal của Expert.
     */
    long countByExpertId(
            Long expertId
    );

    long countByStatus(ProposalStatus status);
    // Đếm tổng số Proposal toàn hệ thống
    @Query("SELECT COUNT(p) FROM Proposal p")
    long countTotalProposals();
    // Đếm tổng số Proposal đã được Chấp nhận (ACCEPTED) toàn hệ thống
    @Query("SELECT COUNT(p) FROM Proposal p WHERE p.status = com.aitasker.common.enums.ProposalStatus.ACCEPTED")
    long countAcceptedProposals();
}