package com.aitasker.admin.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.expert.entity.ServicePackage;
import com.aitasker.expert.exception.ServicePackageNotFoundException;
import com.aitasker.expert.repository.ServicePackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminServiceController {

    private final ServicePackageRepository servicePackageRepository;

    @GetMapping
    public ApiResponse<List<ServicePackage>> getAll() {
        return ApiResponse.success(servicePackageRepository.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<ServicePackage> getById(@PathVariable Long id) {
        return ApiResponse.success(findOrThrow(id));
    }

    @PutMapping("/{id}/approve")
    public ApiResponse<String> approve(@PathVariable Long id) {
        ServicePackage pack = findOrThrow(id);
        pack.setActive(true);
        servicePackageRepository.save(pack);
        return ApiResponse.success("Đã duyệt gói dịch vụ");
    }

    @PutMapping("/{id}/hide")
    public ApiResponse<String> hide(@PathVariable Long id) {
        ServicePackage pack = findOrThrow(id);
        pack.setActive(false);
        servicePackageRepository.save(pack);
        return ApiResponse.success("Đã ẩn gói dịch vụ");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        findOrThrow(id);
        servicePackageRepository.deleteById(id);
        return ApiResponse.success("Đã xoá gói dịch vụ");
    }

    private ServicePackage findOrThrow(Long id) {
        return servicePackageRepository.findById(id)
                .orElseThrow(() -> new ServicePackageNotFoundException("Không tìm thấy gói dịch vụ id: " + id));
    }
}