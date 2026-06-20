// ProposalRepository.java
package com.aitasker.proposal.repository;

import com.aitasker.proposal.entity.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    // Tìm xem có Proposal nào khớp jobId và expertId không
    boolean existsByJob_IdAndExpert_Id(Long jobId, Long expertId);

    // Lấy danh sách Proposal theo ID công việc CÓ HỖ TRỢ PHÂN TRANG
    Page<Proposal> findByJob_Id(Long jobId, Pageable pageable);
}