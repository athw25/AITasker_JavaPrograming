package com.aitasker.delivery.service;

import com.aitasker.delivery.dto.request.SubmitDeliveryRequest;
import com.aitasker.delivery.dto.response.DeliveryResponse;
import com.aitasker.user.entity.User;

import java.util.List;

/** Versioned delivery use cases. */
public interface DeliveryService {
    DeliveryResponse submitDelivery(SubmitDeliveryRequest request, User currentUser);
    List<DeliveryResponse> getMilestoneDeliveries(Long milestoneId, User currentUser);
}
