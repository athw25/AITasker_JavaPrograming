package com.aitasker.expert.mapper;

import com.aitasker.common.mapper.GenericMapper;
import com.aitasker.expert.dto.ExpertDto;
import com.aitasker.expert.entity.ExpertProfile;

public class ExpertMapper implements GenericMapper<ExpertProfile, ExpertDto> {
   @Override
    public ExpertDto toDto(ExpertProfile entity) {
        if (entity == null) {
            return null;
        }
        ExpertDto dto = new ExpertDto();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setTitle(entity.getTitle());
        dto.setSkills(entity.getSkills());
        dto.setExperienceYears(entity.getExperienceYears());
        dto.setHourlyRate(entity.getHourlyRate());
        return dto;
    }
    @Override
    public ExpertProfile toEntity(ExpertDto dto) {
        if (dto == null) {
            return null;
        }
        ExpertProfile entity = new ExpertProfile();
        entity.setId(dto.getId());
        entity.setFullName(dto.getFullName());
        entity.setTitle(dto.getTitle());
        entity.setSkills(dto.getSkills());
        entity.setExperienceYears(dto.getExperienceYears());
        entity.setHourlyRate(dto.getHourlyRate());
        return entity;
    }
}
