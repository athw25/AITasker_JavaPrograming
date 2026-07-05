package com.aitasker.dispute.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aitasker.dispute.entity.DisputeEvidence;

public interface DisputeEvidenceRepository extends JpaRepository<DisputeEvidence, Long> {
    List<DisputeEvidence> findByDisputeId(Long disputeId);
}