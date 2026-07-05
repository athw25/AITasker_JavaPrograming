package com.aitasker.dispute.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aitasker.dispute.entity.DisputeMessage;

public interface DisputeMessageRepository extends JpaRepository<DisputeMessage, Long> {
    List<DisputeMessage> findByDisputeIdOrderByCreatedAtAsc(Long disputeId);
}