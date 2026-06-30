package com.aitasker.milestone.service;

import com.aitasker.milestone.dto.request.CreateMilestoneRequest;
import com.aitasker.milestone.dto.request.SubmitMilestoneRequest;
import com.aitasker.milestone.dto.request.UpdateMilestoneRequest;
import com.aitasker.milestone.dto.response.MilestoneResponse;
import com.aitasker.user.entity.User;

/** Milestone lifecycle use cases. */
public interface MilestoneService {
    MilestoneResponse createMilestone(CreateMilestoneRequest request, User currentUser);
    MilestoneResponse updateMilestone(Long id, UpdateMilestoneRequest request, User currentUser);
    MilestoneResponse submitMilestone(Long id, SubmitMilestoneRequest request, User currentUser);
    MilestoneResponse approveMilestone(Long id, User currentUser);
    MilestoneResponse rejectMilestone(Long id, String reason, User currentUser);
    MilestoneResponse releasePayment(Long id, User currentUser);
}
