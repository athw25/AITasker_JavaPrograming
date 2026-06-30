package com.aitasker.expert.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.expert.dto.request.CreateServicePackageRequest;
import com.aitasker.expert.dto.request.UpdateServicePackageRequest;
import com.aitasker.expert.dto.response.ServicePackageResponse;
import com.aitasker.expert.service.ServicePackageService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServicePackageController {

    private final ServicePackageService packageService;

    public ServicePackageController(ServicePackageService packageService) {
        this.packageService = packageService;
    }

    // API đăng ký một gói dịch vụ mới công khai lên hệ thống
    @PostMapping
    public ApiResponse<ServicePackageResponse> createPackage(@Valid @RequestBody CreateServicePackageRequest request) {
        Long currentUserId = 1L; // Giả lập ID user đang đăng nhập để test
        ServicePackageResponse response = packageService.createPackage(currentUserId, request);
        return ApiResponse.success("Đăng ký gói dịch vụ thành công!", response);
    }

    // API lấy toàn bộ danh sách gói dịch vụ đang hoạt động công khai
    @GetMapping
    public ApiResponse<List<ServicePackageResponse>> getAllPackages() {
        List<ServicePackageResponse> list = packageService.getAllPackages();
        return ApiResponse.success("Lấy danh sách gói dịch vụ thành công!", list);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Chuyên gia cập nhật thông tin/giá tiền của gói dịch vụ")
    public ApiResponse<ServicePackageResponse> updatePackage(@PathVariable Long id, @Valid @RequestBody UpdateServicePackageRequest request) {
        Long currentUserId = 1L;
        ServicePackageResponse response = packageService.updatePackage(currentUserId, id, request);
        return ApiResponse.success("Cập nhật gói dịch vụ thành công!", response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Chuyên gia gỡ bỏ/xóa một gói dịch vụ khỏi hệ thống")
    public ApiResponse<Void> deletePackage(@PathVariable Long id) {
        Long currentUserId = 1L;
        packageService.deletePackage(currentUserId, id);
        return ApiResponse.success("Xóa gói dịch vụ thành công!", null);
    }
}