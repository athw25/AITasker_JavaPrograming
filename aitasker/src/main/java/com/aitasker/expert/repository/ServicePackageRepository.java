package com.aitasker.expert.repository;

import com.aitasker.expert.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    List<ServicePackage> findByExpertId(Long expertId);
}