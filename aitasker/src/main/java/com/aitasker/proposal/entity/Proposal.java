package com.aitasker.proposal.entity;
import com.aitasker.common.entity.BaseEntity;
import com.aitasker.common.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
@Getter
@Setter
public class Proposal extends BaseEntity {
    private Double bidAmount;
    @Column(columnDefinition = "TEXT")
    private String coverLetter;
    private LocalDateTime submittedAt;
    @Enumerated(EnumType.STRING)
    private ProposalStatus status;
}
