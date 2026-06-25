package com.aitasker.project.repository;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.project.entity.Project;
import com.aitasker.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Lấy tất cả project của Client.
     */
    List<Project> findByClientId(Long clientId);

    /**
     * Lấy tất cả project của Expert.
     */
    List<Project> findByExpertId(Long expertId);

    /** Returns every project in which the user is a participant. */
    List<Project> findDistinctByClientIdOrExpertIdOrderByCreatedAtDesc(Long clientId, Long expertId);

    /**
     * Phân trang project của Client.
     */
    Page<Project> findByClientId(Long clientId, Pageable pageable);

    /**
     * Phân trang project của Expert.
     */
    Page<Project> findByExpertId(Long expertId, Pageable pageable);

    /**
     * Lấy project theo proposal.
     */
    Optional<Project> findByProposalId(Long proposalId);

    /**
     * Lấy project theo job.
     */
    Optional<Project> findByJobId(Long jobId);

    /**
     * Kiểm tra project tồn tại theo proposal.
     */
    boolean existsByProposalId(Long proposalId);

    /**
     * Kiểm tra project tồn tại theo job.
     */
    boolean existsByJobId(Long jobId);

    /**
     * Lấy project theo trạng thái.
     */
    List<Project> findByStatus(ProjectStatus status);

    /**
     * Lấy project của Client theo trạng thái.
     */
    List<Project> findByClientIdAndStatus(
            Long clientId,
            ProjectStatus status
    );

    /**
     * Lấy project của Expert theo trạng thái.
     */
    List<Project> findByExpertIdAndStatus(
            Long expertId,
            ProjectStatus status
    );

    /**
     * Đếm số project của Expert.
     */
    long countByExpertId(Long expertId);

    /**
     * Đếm số project hoàn thành của Expert.
     */
    long countByExpertIdAndStatus(
            Long expertId,
            ProjectStatus status
    );

    /**
     * Đếm số project của Client.
     */
    long countByClientId(Long clientId);

    /**
     * Tìm project giữa Client và Expert.
     */
    List<Project> findByClientAndExpert(
            User client,
            User expert
    );
}
