package com.aitasker.expert.service.impl;

import com.aitasker.exception.ForbiddenException;
import com.aitasker.expert.dto.request.CreateServicePackageRequest;
import com.aitasker.expert.dto.response.ServicePackageResponse;
import com.aitasker.expert.entity.ServicePackage;
import com.aitasker.expert.repository.ServicePackageRepository;
import com.aitasker.expert.service.ServicePackageService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicePackageServiceImpl implements ServicePackageService {

    private final ServicePackageRepository packageRepository;

    public ServicePackageServiceImpl(ServicePackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    @Override
    public ServicePackageResponse createPackage(Long currentUserId, CreateServicePackageRequest request) {
        ServicePackage pack = new ServicePackage();
        pack.setExpertId(currentUserId);
        pack.setPackageName(request.getPackageName());
        pack.setPrice(request.getPrice());
        pack.setDeliveryDays(request.getDeliveryDays());

        ServicePackage saved = packageRepository.save(pack);
        return convertToResponse(saved);
    }

    @Override
    public List<ServicePackageResponse> getAllPackages() {
        return packageRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private ServicePackageResponse convertToResponse(ServicePackage entity) {
        ServicePackageResponse res = new ServicePackageResponse();
        res.setId(entity.getId());
        res.setExpertId(entity.getExpertId());
        res.setPackageName(entity.getPackageName());
        res.setPrice(entity.getPrice());
        res.setDeliveryDays(entity.getDeliveryDays());
        return res;
    }

    @Override
    public ServicePackageResponse updatePackage(Long currentUserId, Long packageId, com.aitasker.expert.dto.request.UpdateServicePackageRequest request) {
        ServicePackage pack = packageRepository.findById(packageId)
                .orElseThrow(() -> new com.aitasker.expert.exception.ServicePackageNotFoundException("Không tìm thấy gói dịch vụ cần sửa!"));
        
        // Kiểm tra quyền sở hữu (Security Integration)
        if (!pack.getExpertId().equals(currentUserId)) {
            // Trước đây ném ExpertNotFoundException (không có handler riêng
            // -> rơi vào nhánh 500 chung) dù bản chất đây là lỗi 403 Forbidden.
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa gói dịch vụ của người khác!");
        }

        pack.setPackageName(request.getPackageName());
        pack.setPrice(request.getPrice());
        pack.setDeliveryDays(request.getDeliveryDays());

        ServicePackage saved = packageRepository.save(pack);
        return convertToResponse(saved);
    }

    @Override
    public void deletePackage(Long currentUserId, Long packageId) {
        ServicePackage pack = packageRepository.findById(packageId)
                .orElseThrow(() -> new com.aitasker.expert.exception.ServicePackageNotFoundException("Không tìm thấy gói dịch vụ cần xóa!"));
        
        // Kiểm tra quyền sở hữu (Security Integration)
        if (!pack.getExpertId().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền xóa gói dịch vụ của người khác!");
        }
        packageRepository.deleteById(packageId);
    }
}