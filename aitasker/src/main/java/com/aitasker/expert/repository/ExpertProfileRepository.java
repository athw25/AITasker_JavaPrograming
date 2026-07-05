package com.aitasker.expert.repository;

import com.aitasker.expert.entity.ExpertProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertProfileRepository extends JpaRepository<ExpertProfile, Long>, JpaSpecificationExecutor<ExpertProfile> {
    Optional<ExpertProfile> findByUserId(Long userId);
    Optional<ExpertProfile> findByUserEmail(String email);
}