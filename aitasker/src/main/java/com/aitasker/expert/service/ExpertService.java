package com.aitasker.expert.service;

import com.aitasker.expert.dto.request.UpdateExpertProfileRequest;
import com.aitasker.expert.dto.response.ExpertProfileResponse;

public interface ExpertService {
    ExpertProfileResponse getMyProfile(Long currentUserId);
    ExpertProfileResponse updateProfile(Long currentUserId, UpdateExpertProfileRequest request);
}
