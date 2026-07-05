package com.aitasker.dispute.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.dispute.entity.Dispute;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    Page<Dispute> findByStatus(DisputeStatus status, Pageable pageable);
    Page<Dispute> findByProjectId(Long projectId, Pageable pageable);
    boolean existsByProjectIdAndStatusIn(Long projectId, List<DisputeStatus> statuses);
    Optional<Dispute> findByIdAndStatus(Long id, DisputeStatus status);
}