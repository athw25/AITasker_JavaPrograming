// ProposalResponseDTO.java
package com.aitasker.proposal.dto.response;

import com.aitasker.common.enums.ProposalStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProposalResponseDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long expertId;
    private String expertName;
    private Double bidAmount;
    private String coverLetter;
    private ProposalStatus status;
    private LocalDateTime submittedAt;
}