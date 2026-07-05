package com.aitasker.analytics.entity;

import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "analytics_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsEvent extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String eventType;

    private Long refId;

    @Column(columnDefinition = "TEXT")
    private String metadata;
}
