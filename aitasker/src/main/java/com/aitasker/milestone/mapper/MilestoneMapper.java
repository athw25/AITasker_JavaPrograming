package com.aitasker.milestone.mapper;

import com.aitasker.delivery.entity.Delivery;
import com.aitasker.milestone.dto.response.MilestoneResponse;
import com.aitasker.milestone.entity.Milestone;
import org.springframework.stereotype.Component;

@Component
public class MilestoneMapper {

    public MilestoneResponse toResponse(
            Milestone milestone
    ) {

        if (milestone == null) {
            return null;
        }

        int totalDeliveries = 0;
        int latestVersion = 0;

        if (milestone.getDeliveries() != null) {

            totalDeliveries =
                    milestone.getDeliveries().size();

            for (Delivery delivery :
                    milestone.getDeliveries()) {

                if (delivery.getVersion()
                        > latestVersion) {

                    latestVersion =
                            delivery.getVersion();
                }
            }
        }

        return MilestoneResponse.builder()

                .id(milestone.getId())

                .projectId(
                        milestone.getProject().getId()
                )

                .title(milestone.getTitle())
                .description(
                        milestone.getDescription()
                )

                .amount(milestone.getAmount())

                .dueDate(milestone.getDueDate())

                .status(milestone.getStatus())

                .submittedAt(
                        milestone.getSubmittedAt()
                )

                .approvedAt(
                        milestone.getApprovedAt()
                )

                .totalDeliveries(totalDeliveries)
                .latestVersion(latestVersion)

                .paid(
                        milestone.getStatus() != null
                                && milestone.getStatus()
                                .name()
                                .equals("PAID")
                )

                .createdAt(
                        milestone.getCreatedAt()
                )

                .updatedAt(
                        milestone.getUpdatedAt()
                )

                .build();
    }
}