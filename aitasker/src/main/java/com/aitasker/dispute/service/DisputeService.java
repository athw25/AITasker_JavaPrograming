package com.aitasker.dispute.service;

import com.aitasker.common.response.PageResponse;
import com.aitasker.dispute.dto.request.AddDisputeMessageRequest;
import com.aitasker.dispute.dto.request.AddEvidenceRequest;
import com.aitasker.dispute.dto.request.CreateDisputeRequest;
import com.aitasker.dispute.dto.request.DisputeResolveRequest;
import com.aitasker.dispute.dto.response.DisputeResponse;
import com.aitasker.user.entity.User;

public interface DisputeService {

    DisputeResponse createDispute(CreateDisputeRequest request, User currentUser);

    DisputeResponse getDisputeById(Long id, User currentUser);

    PageResponse<DisputeResponse> getDisputes(String status, int page, int size, User currentUser);

    DisputeResponse resolveDispute(Long id, DisputeResolveRequest request, User currentUser);

    DisputeResponse addEvidence(Long id, AddEvidenceRequest request, User currentUser);

    DisputeResponse addMessage(Long id, AddDisputeMessageRequest request, User currentUser);
}