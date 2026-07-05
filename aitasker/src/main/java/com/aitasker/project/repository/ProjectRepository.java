package com.aitasker.project.repository;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.project.entity.Project;
import com.aitasker.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

        /**
         * Lấy tất cả Project của Client.
         */
        List<Project> findByClient_Id(Long clientId);

        /**
         * Lấy tất cả Project của Expert.
         */
        List<Project> findByExpert_Id(Long expertId);

        /**
         * Lấy tất cả Project mà User tham gia.
         */
        List<Project> findDistinctByClient_IdOrExpert_IdOrderByCreatedAtDesc(
                        Long clientId,
                        Long expertId);

        /**
         * Phân trang Project của Client.
         */
        Page<Project> findByClient_Id(
                        Long clientId,
                        Pageable pageable);

        /**
         * Phân trang Project của Expert.
         */
        Page<Project> findByExpert_Id(
                        Long expertId,
                        Pageable pageable);

        /**
         * Tìm Project theo Proposal.
         */
        Optional<Project> findByProposal_Id(Long proposalId);

        /**
         * Tìm Project theo Job.
         */
        Optional<Project> findByJob_Id(Long jobId);

        /**
         * Kiểm tra Project đã tồn tại theo Proposal chưa.
         */
        boolean existsByProposal_Id(Long proposalId);

        /**
         * Kiểm tra Project đã tồn tại theo Job chưa.
         */
        boolean existsByJob_Id(Long jobId);

        /**
         * Lấy Project theo trạng thái.
         */
        List<Project> findByStatus(ProjectStatus status);

        /**
         * Lấy Project của Client theo trạng thái.
         */
        List<Project> findByClient_IdAndStatus(
                        Long clientId,
                        ProjectStatus status);

        /**
         * Lấy Project của Expert theo trạng thái.
         */
        List<Project> findByExpert_IdAndStatus(
                        Long expertId,
                        ProjectStatus status);

        /**
         * Đếm tổng Project của Expert.
         */
        long countByExpert_Id(Long expertId);

        /**
         * Đếm Project của Expert theo trạng thái.
         */
        long countByExpert_IdAndStatus(
                        Long expertId,
                        ProjectStatus status);

        /**
         * Đếm tổng Project của Client.
         */
        long countByClient_Id(Long clientId);

        /**
         * Tìm Project giữa Client và Expert.
         */
        List<Project> findByClientAndExpert(
                        User client,
                        User expert);

        // Tính tổng số dự án theo từng chuyên gia
        @Query("SELECT p.expert.id, COUNT(p) FROM Project p GROUP BY p.expert.id")
        List<Object[]> getTotalProjectsCountForExperts();

        // Tính tổng số dự án đã HOÀN THÀNH (COMPLETED) theo từng chuyên gia
        @Query("SELECT p.expert.id, COUNT(p) FROM Project p WHERE p.status = com.aitasker.common.enums.ProjectStatus.COMPLETED GROUP BY p.expert.id")
        List<Object[]> getCompletedProjectsCountForExperts();
}