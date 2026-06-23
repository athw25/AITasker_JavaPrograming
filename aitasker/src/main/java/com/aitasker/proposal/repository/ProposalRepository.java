package com.aitasker.proposal.repository;

import com.aitasker.proposal.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for expert proposals. */
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
