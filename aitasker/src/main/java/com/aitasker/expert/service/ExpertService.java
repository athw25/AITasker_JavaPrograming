package com.aitasker.expert.service;

import com.aitasker.common.util.ValidationUtils;
import com.aitasker.expert.dto.ExpertDto;
import com.aitasker.expert.entity.ExpertProfile;
import com.aitasker.expert.mapper.ExpertMapper;
import java.util.ArrayList;
import java.util.List;

public class ExpertService {
    // Giả lập danh sách bộ nhớ tạm thay cho Database để chạy demo
    private final List<ExpertProfile> expertDatabase = new ArrayList<>();
    private final ExpertMapper expertMapper = new ExpertMapper();

    /**
     * Nghiệp vụ 1: Tạo mới hồ sơ chuyên gia (Có kiểm tra dữ liệu trống)
     */
    public String createExpertProfile(ExpertProfile profile) {
        // Tích hợp ValidationUtils từ gói common của bạn
        if (ValidationUtils.isBlank(profile.getFullName())) {
            return "Tên chuyên gia không được để trống!";
        }
        if (ValidationUtils.isBlank(profile.getTitle())) {
            return "Vị trí chuyên môn không được để trống!";
        }
        
        expertDatabase.add(profile);
        return "SUCCESS";
    }

    /**
     * Nghiệp vụ 2: Lấy chi tiết hồ sơ chuyên gia bằng ID (Có chuyển đổi sang DTO an toàn)
     */
    public ExpertDto getExpertById(Long id) {
        for (ExpertProfile profile : expertDatabase) {
            if (profile.getId().equals(id)) {
                // Tích hợp ExpertMapper để gọt tỉa dữ liệu nhạy cảm trước khi trả về
                return expertMapper.toDto(profile);
            }
        }
        return null; // Không tìm thấy
    }

    /**
     * Nghiệp vụ 3: Lấy toàn bộ danh sách chuyên gia
     */
    public List<ExpertDto> getAllExperts() {
        return expertMapper.toDtoList(expertDatabase);
    }
}
