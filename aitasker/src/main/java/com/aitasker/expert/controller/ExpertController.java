package com.aitasker.expert.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.expert.dto.ExpertDto;
import com.aitasker.expert.entity.ExpertProfile;
import com.aitasker.expert.service.ExpertService;
import java.util.List;

/** Nơi tiếp nhận các yêu cầu API liên quan đến Chuyên gia AI từ giao diện Frontend.*/
public class ExpertController {

    private final ExpertService expertService = new ExpertService();

    /**
     * API 1: Đăng ký hồ sơ chuyên gia
     */
    public ApiResponse<String> registerExpert(ExpertProfile profile) {
        String result = expertService.createExpertProfile(profile);
        
        if (!"SUCCESS".equals(result)) {
            // Trả về hộp lỗi chuẩn hóa
            return ApiResponse.error(result);
        }
        
        // Trả về hộp thành công chuẩn hóa
        return ApiResponse.success("Đăng ký hồ sơ chuyên gia AI thành công!", null);
    }

    /**
     * API 2: Xem chi tiết 1 chuyên gia trên Chợ (Marketplace)
     */
    public ApiResponse<ExpertDto> viewExpertDetail(Long id) {
        ExpertDto expertDto = expertService.getExpertById(id);
        
        if (expertDto == null) {
            return ApiResponse.error("Không tìm thấy thông tin chuyên gia AI này!");
        }
        
        return ApiResponse.success("Lấy thông tin chuyên gia thành công!", expertDto);
    }

    /**
     * API 3: Xem toàn bộ danh sách chuyên gia công khai
     */
    public ApiResponse<List<ExpertDto>> viewAllExperts() {
        List<ExpertDto> experts = expertService.getAllExperts();
        return ApiResponse.success("Tải danh sách chuyên gia thành công!", experts);
    }
}