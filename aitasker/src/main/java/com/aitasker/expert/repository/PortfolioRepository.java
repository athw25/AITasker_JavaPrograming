package com.aitasker.expert.repository;

import com.aitasker.expert.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByExpert_Id(Long expertId);

    // Tính số lượng sản phẩm/dự án nổi bật của từng chuyên gia
    @Query("SELECT p.expert.id, COUNT(p) FROM Portfolio p GROUP BY p.expert.id")
    List<Object[]> getPortfolioCountsForExperts();
}
