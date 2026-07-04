package com.aitasker.expert.service.impl;

import com.aitasker.expert.dto.request.UpdateExpertProfileRequest;
import com.aitasker.expert.dto.response.ExpertProfileResponse;
import com.aitasker.expert.entity.ExpertProfile;
import com.aitasker.expert.mapper.ExpertMapper;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.expert.service.ExpertService;
import org.springframework.stereotype.Service;

@Service
public class ExpertServiceImpl implements ExpertService {

    private final ExpertProfileRepository expertRepository;
    private final ExpertMapper expertMapper;

    // Dependency Injection qua Constructor chuẩn dự án
    public ExpertServiceImpl(ExpertProfileRepository expertRepository, ExpertMapper expertMapper) {
        this.expertRepository = expertRepository;
        this.expertMapper = expertMapper;
    }

    @Override
    public ExpertProfileResponse getMyProfile(Long currentUserId) {
        ExpertProfile profile = expertRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ chuyên gia!"));
        return expertMapper.toDto(profile);
    }

    @Override
    public ExpertProfileResponse updateProfile(Long currentUserId, UpdateExpertProfileRequest request) {
        ExpertProfile profile = expertRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ chuyên gia!"));
        
        // Cập nhật dữ liệu từ Request lên Entity dữ liệu thật
        profile.setFullName(request.getFullName());
        profile.setTitle(request.getTitle());
        profile.setSkills(request.getSkills());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setHourlyRate(request.getHourlyRate());
        
        ExpertProfile savedProfile = expertRepository.save(profile);
        return expertMapper.toDto(savedProfile);
    }
}