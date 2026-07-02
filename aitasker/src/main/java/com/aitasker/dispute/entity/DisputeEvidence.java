package com.aitasker.dispute.entity;

import com.aitasker.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "dispute_evidences",
        indexes = {
                @Index(name = "idx_dispute_evidence_dispute", columnList = "dispute_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisputeEvidence extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "dispute_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dispute_evidence_dispute")
    )
    private Dispute dispute;
    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(columnDefinition = "nvarchar(max)")
    private String description;
}