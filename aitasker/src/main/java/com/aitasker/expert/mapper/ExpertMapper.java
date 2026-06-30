package com.aitasker.expert.mapper;

import com.aitasker.common.mapper.GenericMapper;
import com.aitasker.expert.dto.request.UpdateExpertProfileRequest;
import com.aitasker.expert.dto.response.ExpertProfileResponse;
import com.aitasker.expert.entity.ExpertProfile;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExpertMapper implements GenericMapper<ExpertProfile, ExpertProfileResponse> {

    @Override
    public ExpertProfileResponse toDto(ExpertProfile entity) {
        if (entity == null) return null;

        ExpertProfileResponse response = new ExpertProfileResponse();
        response.setId(entity.getId());
        response.setFullName(entity.getFullName());
        response.setTitle(entity.getTitle());
        response.setSkills(entity.getSkills());
        response.setExperienceYears(entity.getExperienceYears());
        response.setHourlyRate(entity.getHourlyRate());
        
        // internalNotes tuyệt đối KHÔNG đưa vào đây để bảo mật dữ liệu theo yêu cầu của TL
        
        return response;
    }

    @Override
    public ExpertProfile toEntity(ExpertProfileResponse dto) {
        if (dto == null) return null;

        ExpertProfile entity = new ExpertProfile();
        entity.setId(dto.getId());
        entity.setFullName(dto.getFullName());
        entity.setTitle(dto.getTitle());
        entity.setSkills(dto.getSkills());
        entity.setExperienceYears(dto.getExperienceYears());
        entity.setHourlyRate(dto.getHourlyRate());
        return entity;
    }

    // Hàm phụ trợ chuyển đổi từ Request nhập vào sang Entity để lưu DB
    public ExpertProfile requestToEntity(UpdateExpertProfileRequest request) {
        if (request == null) return null;

        ExpertProfile entity = new ExpertProfile();
        entity.setFullName(request.getFullName());
        entity.setTitle(request.getTitle());
        entity.setSkills(request.getSkills());
        entity.setExperienceYears(request.getExperienceYears());
        entity.setHourlyRate(request.getHourlyRate());
        return entity;
    }

    @Override
    public List<ExpertProfileResponse> toDtoList(List<ExpertProfile> entityList) {
        if (entityList == null) return null;
        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ExpertProfile> toEntityList(List<ExpertProfileResponse> dtoList) {
        if (dtoList == null) return null;
        return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
    }
}