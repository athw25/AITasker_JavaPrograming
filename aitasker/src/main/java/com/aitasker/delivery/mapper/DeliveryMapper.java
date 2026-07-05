package com.aitasker.delivery.mapper;

import com.aitasker.delivery.dto.response.DeliveryResponse;
import com.aitasker.delivery.entity.Delivery;
import org.springframework.stereotype.Component;

@Component
public class DeliveryMapper {

    public DeliveryResponse toResponse(
            Delivery delivery
    ) {

        if (delivery == null) {
            return null;
        }

        return DeliveryResponse.builder()

                .id(delivery.getId())

                .milestoneId(
                        delivery.getMilestone().getId()
                )

                .milestoneTitle(
                        delivery.getMilestone().getTitle()
                )

                .fileUrl(delivery.getFileUrl())

                .note(delivery.getNote())

                .version(delivery.getVersion())

                .status(delivery.getStatus())

                .submittedById(
                        delivery.getSubmittedBy().getId()
                )

                .submittedByName(
                        delivery.getSubmittedBy().getName()
                )

                .submittedAt(
                        delivery.getSubmittedAt()
                )

                .approvedAt(
                        delivery.getApprovedAt()
                )

                .rejectReason(
                        delivery.getRejectReason()
                )

                .createdAt(
                        delivery.getCreatedAt()
                )

                .updatedAt(
                        delivery.getUpdatedAt()
                )

                .build();
    }
}