package com.aitasker.analytics.entity;

import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemMetric extends BaseEntity {

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", precision = 20, scale = 4)
    private BigDecimal metricValue;

    @Column(name = "metric_unit", length = 30)
    private String metricUnit;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onRecordedAt() {
        if (recordedAt == null) recordedAt = LocalDateTime.now();
    }
}