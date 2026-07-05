package com.aitasker.dispute.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.user.entity.User;

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
        name = "dispute_messages",
        indexes = {
                @Index(name = "idx_dispute_message_dispute", columnList = "dispute_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisputeMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "dispute_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dispute_message_dispute")
    )
    private Dispute dispute;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sender_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dispute_message_sender")
    )
    private User sender;

    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String message;
}