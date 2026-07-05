package com.aitasker.analytics.repository;

import com.aitasker.analytics.entity.AnalyticsEvent;
import com.aitasker.analytics.enums.AnalyticsEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {

    List<AnalyticsEvent> findByEventType(AnalyticsEventType eventType);

    List<AnalyticsEvent> findByActorId(Long actorId);

    List<AnalyticsEvent> findByEntityTypeAndEntityId(String entityType, String entityId);

    long countByEventType(AnalyticsEventType eventType);

    long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    long countByEventTypeAndCreatedAtBetween(
            AnalyticsEventType eventType,
            LocalDateTime from,
            LocalDateTime to
    );

    @Query("SELECT e.eventType, COUNT(e) FROM AnalyticsEvent e " +
           "WHERE e.createdAt >= :from GROUP BY e.eventType ORDER BY COUNT(e) DESC")
    List<Object[]> countGroupByEventTypeSince(@Param("from") LocalDateTime from);
}