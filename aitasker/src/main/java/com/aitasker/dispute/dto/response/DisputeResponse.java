package com.aitasker.dispute.dto.response;

import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.dispute.entity.Dispute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisputeResponse {
    private Long id;
    private Long projectId;
    private Long milestoneId;
    private Long creatorId;
    private String creatorName;
    private String reason;
    private DisputeStatus status;
    private String resolution;
    private LocalDateTime createdAt;

    public static DisputeResponse from(Dispute d) {
        return new DisputeResponse(
                d.getId(),
                d.getProject() != null ? d.getProject().getId() : null,
                d.getMilestone() != null ? d.getMilestone().getId() : null,
                d.getCreator() != null ? d.getCreator().getId() : null,
                d.getCreator() != null ? d.getCreator().getName() : null,
                d.getReason(),
                d.getStatus(),
                d.getResolution(),
                d.getCreatedAt()
        );
    }
}
