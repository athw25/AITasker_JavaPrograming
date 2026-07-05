package com.aitasker.dispute.repository;

import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.dispute.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByStatus(DisputeStatus status);
    List<Dispute> findByCreator_Id(Long creatorId);
    List<Dispute> findByProject_Id(Long projectId);
    Optional<Dispute> findFirstByProject_IdAndStatusIn(Long projectId, List<DisputeStatus> statuses);
}
