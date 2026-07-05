package com.aitasker.analytics.repository;

import com.aitasker.analytics.entity.SystemMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemMetricRepository extends JpaRepository<SystemMetric, Long> {

    List<SystemMetric> findByMetricNameOrderByRecordedAtDesc(String metricName);

    List<SystemMetric> findByCategory(String category);

    Optional<SystemMetric> findTopByMetricNameOrderByRecordedAtDesc(String metricName);

    @Query("SELECT m FROM SystemMetric m WHERE m.metricName = :name AND m.recordedAt >= :from ORDER BY m.recordedAt ASC")
    List<SystemMetric> findByMetricNameSince(
            @Param("name") String name,
            @Param("from") LocalDateTime from
    );
}