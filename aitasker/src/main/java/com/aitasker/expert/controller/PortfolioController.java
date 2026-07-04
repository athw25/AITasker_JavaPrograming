package com.aitasker.expert.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.expert.dto.request.CreatePortfolioRequest;
import com.aitasker.expert.dto.response.PortfolioResponse;
import com.aitasker.expert.service.PortfolioService;
import com.aitasker.security.userdetails.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/experts/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // API thêm mới một dự án vào Portfolio
    @PostMapping
    @PreAuthorize("hasRole('EXPERT')")
    public ApiResponse<PortfolioResponse> addPortfolio(
            @Valid @RequestBody CreatePortfolioRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Long currentUserId = principal.getUser().getId();
        PortfolioResponse response = portfolioService.addPortfolio(currentUserId, request);
        return ApiResponse.success("Thêm dự án vào portfolio thành công!", response);
    }

    // API lấy danh sách portfolio của một expert (public, không cần đăng nhập)
    @GetMapping("/{expertId}")
    public ApiResponse<List<PortfolioResponse>> getPortfolios(@PathVariable Long expertId) {
        List<PortfolioResponse> list = portfolioService.getPortfoliosByExpert(expertId);
        return ApiResponse.success("Lấy danh sách portfolio thành công!", list);
    }

    // API xóa một dự án khỏi Portfolio dựa theo ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EXPERT')")
    public ApiResponse<Void> deletePortfolio(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        Long currentUserId = principal.getUser().getId();
        portfolioService.deletePortfolio(currentUserId, id);
        return ApiResponse.success("Xóa dự án khỏi portfolio thành công!", null);
    }
}