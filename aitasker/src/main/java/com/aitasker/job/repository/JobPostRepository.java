package com.aitasker.job.repository;

import com.aitasker.common.enums.JobStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.job.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByClientId(Long clientId);
    List<JobPost> findByStatus(JobStatus status);
    long countByStatus(JobStatus status);
    @Query("""
        SELECT j FROM JobPost j
        WHERE (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:skills IS NULL OR LOWER(j.requiredSkills) LIKE LOWER (CONCAT('%', :skills,'%')))
        AND (:minBudget IS NULL OR j.budget >= :minBudget)
        AND (:maxBudget IS NULL OR j.budget <= :maxBudget)
        AND (:status IS NULL OR j.status = :status)
    """)

    List<JobPost> search(
      @Param("keyword") String keyword,
      @Param("skills") String skills,
      @Param("minBudget") Double minBudget,
      @Param("maxBudget") Double maxBudget,
      @Param("status") JobStatus status
    );
}
