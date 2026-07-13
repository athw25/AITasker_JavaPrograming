package com.aitasker.dispute.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.dispute.entity.Dispute;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    Page<Dispute> findByStatus(DisputeStatus status, Pageable pageable);
    Page<Dispute> findByProjectId(Long projectId, Pageable pageable);
    boolean existsByProjectIdAndStatusIn(Long projectId, List<DisputeStatus> statuses);
    Optional<Dispute> findByIdAndStatus(Long id, DisputeStatus status);

    // Lọc quyền truy cập NGAY trong query để phân trang chính xác
    // (thay vì filter trong bộ nhớ sau khi đã phân trang ở DB).
    @Query("select d from Dispute d where d.project.client.id = :uid or d.project.expert.id = :uid")
    Page<Dispute> findByParticipant(@Param("uid") Long userId, Pageable pageable);

    @Query("select d from Dispute d where d.status = :status " +
           "and (d.project.client.id = :uid or d.project.expert.id = :uid)")
    Page<Dispute> findByStatusAndParticipant(@Param("status") DisputeStatus status,
                                              @Param("uid") Long userId, Pageable pageable);
}