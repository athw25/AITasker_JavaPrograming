package com.aitasker.expert.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.expert.dto.request.UpdateExpertProfileRequest;
import com.aitasker.expert.dto.response.ExpertProfileResponse;
import com.aitasker.expert.service.ExpertService;
import com.aitasker.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/experts")
@Tag(name = "Expert Profile Module", description = "Quản lý thông tin hồ sơ cá nhân của Chuyên gia")
public class ExpertController {

    private final ExpertService expertService;

    public ExpertController(ExpertService expertService) {
        this.expertService = expertService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Lấy thông tin hồ sơ của Chuyên gia đang đăng nhập")
    public ApiResponse<ExpertProfileResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails principal) {
        Long currentUserId = principal.getUser().getId();
        ExpertProfileResponse profile = expertService.getMyProfile(currentUserId);
        return ApiResponse.success("Lấy thông tin hồ sơ chuyên gia thành công!", profile);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Cập nhật hồ sơ thông tin cá nhân của Chuyên gia")
    public ApiResponse<ExpertProfileResponse> updateProfile(
            @Valid @RequestBody UpdateExpertProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Long currentUserId = principal.getUser().getId();
        ExpertProfileResponse updated = expertService.updateProfile(currentUserId, request);
        return ApiResponse.success("Cập nhật hồ sơ chuyên gia thành công!", updated);
    }
}