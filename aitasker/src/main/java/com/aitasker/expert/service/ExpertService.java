package com.aitasker.expert.service;

import com.aitasker.expert.dto.request.UpdateExpertProfileRequest;
import com.aitasker.expert.dto.response.ExpertProfileResponse;


public interface ExpertService {
    ExpertProfileResponse getMyProfile(Long currentUserId);
    ExpertProfileResponse updateProfile(Long currentUserId, UpdateExpertProfileRequest request);
    java.util.List<com.aitasker.expert.dto.response.ExpertProfileResponse> searchExpertsAdvanced(String skill, Double minRating, Integer minExperience);
}
