package com.aitasker.milestone.repository;

import com.aitasker.common.enums.MilestoneStatus;
import com.aitasker.milestone.entity.Milestone;
import com.aitasker.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface MilestoneRepository
        extends JpaRepository<Milestone, Long> {

    /** Locks a milestone while allocating a delivery version or changing state. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Milestone m where m.id = :id")
    Optional<Milestone> findByIdForUpdate(@Param("id") Long id);

    /**
     * Lấy tất cả milestone của project.
     */
    List<Milestone> findByProjectId(Long projectId);

    /**
     * Lấy milestone theo project.
     */
    List<Milestone> findByProject(Project project);

    /**
     * Lấy milestone theo project theo thứ tự ID.
     */
    List<Milestone> findByProjectIdOrderByIdAsc(Long projectId);

    /**
     * Lấy milestone theo trạng thái.
     */
    List<Milestone> findByStatus(
            MilestoneStatus status
    );

    /**
     * Lấy milestone theo project và trạng thái.
     */
    List<Milestone> findByProjectIdAndStatus(
            Long projectId,
            MilestoneStatus status
    );

    /**
     * Đếm milestone theo trạng thái.
     */
    long countByProjectIdAndStatus(
            Long projectId,
            MilestoneStatus status
    );

    /**
     * Đếm tổng milestone của project.
     */
    long countByProjectId(Long projectId);

    /**
     * Kiểm tra milestone thuộc project.
     */
    boolean existsByIdAndProjectId(
            Long milestoneId,
            Long projectId
    );

    /**
     * Lấy milestone mới nhất.
     */
    Optional<Milestone>
    findTopByProjectIdOrderByCreatedAtDesc(
            Long projectId
    );
}
